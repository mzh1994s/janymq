package cn.mzhong.janytask.core;

import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.tool.PInvoker;
import cn.mzhong.janytask.util.Arrays;

import java.util.*;
import java.util.concurrent.*;

/**
 * 任务调度器
 *
 * @author mzhong
 * @date 2019年7月18日
 * @since 1.0.1
 */
public class TaskWorker extends Thread {

    protected volatile boolean isShutdown = false;

    protected Set<TaskExecutor> taskExecutors = new HashSet<TaskExecutor>();

    protected Map<Date, TaskExecutor[]> waitingExecutors = new ConcurrentHashMap<Date, TaskExecutor[]>();

    protected ExecutorService executors = Executors.newCachedThreadPool(new ThreadFactory() {
        int cnt = 0;

        public Thread newThread(Runnable r) {
            return new Thread(r, "janytask-executor" + (cnt++));
        }
    });

    protected PInvoker<TaskExecutor> onExecutorCompleteLinstener = new PInvoker<TaskExecutor>() {

        public void invoke(TaskExecutor executor) throws Exception {
            TaskWorker.this.addTimedExecutor(executor);
        }
    };

    public void addTimedExecutor(TaskExecutor executor) {
        Date executeTime = executor.getNext();
        TaskExecutor[] taskExecutors = waitingExecutors.get(executeTime);
        if (taskExecutors == null) {
            taskExecutors = new TaskExecutor[]{executor};
        } else {
            taskExecutors = Arrays.add(taskExecutors, executor);
        }
        waitingExecutors.put(executeTime, taskExecutors);
    }

    public void addExecutor(TaskExecutor executor) {
        executor.setOnCompleteListener(onExecutorCompleteLinstener);
        this.addTimedExecutor(executor);
        this.taskExecutors.add(executor);
    }

    public void addExecutors(Set<TaskExecutor> executors) {
        Iterator<TaskExecutor> iterator = executors.iterator();
        while (iterator.hasNext()) {
            addExecutor(iterator.next());
        }
    }

    public TaskWorker() {
        this.setName("janytask-worker");
    }

    protected TaskExecutor[] getNexts(Date nextTime) {
        return this.waitingExecutors.remove(nextTime);
    }

    protected Date getNextTime() {
        Date nextTime = null;
        Set<Date> nextTimes = waitingExecutors.keySet();
        Iterator<Date> iterator = nextTimes.iterator();
        Date currentTime = new Date();
        while (iterator.hasNext()) {
            Date next = iterator.next();
            if (next.before(currentTime)) {
                nextTime = next;
            }
        }
        return nextTime;
    }

    protected void waiting(long millis) {
        // wait(0)会永久阻塞，wait(-n)会异常
        if (millis > 0) {
            try {
                synchronized (this) {
                    this.wait(millis);
                }
            } catch (Exception e) {
                // pass
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            // 获取下一个时间点
            Date nextTime = this.getNextTime();
            // 获取不到下一个时间点则说明任务都繁忙
            if (nextTime == null) {
                this.waiting(1000);
                continue;
            }
            // 时间点等待
            this.waiting(nextTime.getTime() - System.currentTimeMillis());
            if (this.isShutdown) {
                break;
            }
            // 获取下一个时间点需要执行的Executor
            TaskExecutor[] nexts = this.getNexts(nextTime);
            // 执行每一个Executor
            int len = nexts.length;
            for (int i = 0; i < len; i++) {
                executors.execute(nexts[i]);
            }

        }
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public boolean shutdownAndAwait() throws InterruptedException {
        this.isShutdown = true;
        synchronized (this) {
            this.notify();
        }
        Iterator<TaskExecutor> iterator = taskExecutors.iterator();
        while (iterator.hasNext()) {
            TaskExecutor next = iterator.next();
            synchronized (next) {
                next.notify();
            }
        }
        this.executors.shutdown();
        return this.executors.awaitTermination(1000, TimeUnit.SECONDS);
    }
}
