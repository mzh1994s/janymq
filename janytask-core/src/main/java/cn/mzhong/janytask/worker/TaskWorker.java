package cn.mzhong.janytask.worker;

import cn.mzhong.janytask.application.TaskComponent;
import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.tool.PInvoker;
import cn.mzhong.janytask.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 任务调度器，使用时间点分组调度
 *
 * @author mzhong
 * @date 2019年7月18日
 * @since 2.0.0
 */
public class TaskWorker extends Thread implements TaskComponent {

    final static Logger Log = LoggerFactory.getLogger(TaskWorker.class);

    // 空闲延时
    private final static int IDLE_INTERVAL = 1000;

    // 上下文对象
    protected TaskContext context;

    // 任务执行者列表（终结任务时用于notify）
    protected Set<TaskExecutor> taskExecutors = new HashSet<TaskExecutor>();

    // 等待中的任务执行者，根据时间点分组
    protected Map<Date, TaskExecutor[]> waitingExecutors = new ConcurrentHashMap<Date, TaskExecutor[]>();

    // 调度器线程池
    protected ExecutorService executors = Executors.newCachedThreadPool(new ThreadFactory() {
        int cnt = 0;

        public Thread newThread(Runnable r) {
            return new Thread(r, "janytask-executor" + (cnt++));
        }
    });

    // 执行者结束时的监听回调
    protected PInvoker<TaskExecutor> onExecutorCompleteLinstener = new PInvoker<TaskExecutor>() {

        public void invoke(TaskExecutor executor) throws Exception {
            TaskWorker.this.addTimedExecutor(executor);
        }
    };

    public TaskWorker() {
        this.setName("janytask-worker");
    }

    public void setContext(TaskContext context) {
        this.context = context;
    }

    /**
     * 添加一个任务执行者到等待任务分组中，如果没有当前时间点的分组则会创建一个分组
     *
     * @param executor
     */
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

    /**
     * 添加任务执行者到列表
     *
     * @param executor
     */
    public void addExecutor(TaskExecutor executor) {
        executor.setOnCompleteListener(onExecutorCompleteLinstener);
        this.addTimedExecutor(executor);
        this.taskExecutors.add(executor);
    }

    /**
     * 添加多个任务执行者到列表
     *
     * @param executors
     */
    public void addExecutors(Set<TaskExecutor> executors) {
        Iterator<TaskExecutor> iterator = executors.iterator();
        while (iterator.hasNext()) {
            addExecutor(iterator.next());
        }
    }

    /**
     * 获取下一组需要执行的executor
     *
     * @param nextTime
     * @return
     */
    protected TaskExecutor[] getNextExecutors(Date nextTime) {
        return this.waitingExecutors.remove(nextTime);
    }

    /**
     * 获取下一个执行时间
     *
     * @return
     */
    protected Date getNextTime() {
        Date nextTime = null;
        Set<Date> nextTimes = waitingExecutors.keySet();
        Iterator<Date> iterator = nextTimes.iterator();
        if (iterator.hasNext()) {
            nextTime = iterator.next();
        }
        while (iterator.hasNext()) {
            Date next = iterator.next();
            if (next.before(nextTime)) {
                nextTime = next;
            }
        }
        return nextTime;
    }

    /**
     * 等待时间，sleep的替代方式
     *
     * @param millis
     */
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
        // 永久循环，直到shutdown命令到来
        while (true) {
            // 获取下一个时间点
            Date nextTime = this.getNextTime();
            // 获取不到下一个时间点则说明任务都繁忙
            if (nextTime == null) {
                // 空闲阻塞
                this.waiting(IDLE_INTERVAL);
            } else {
                // 计算需要等待的时间
                long waiting = nextTime.getTime() - System.currentTimeMillis();
                if (waiting > IDLE_INTERVAL) {
                    // 如果等待时间大于空闲时间，则进行空闲阻塞，避免耗时阻塞影响其他业务
                    // 但是为了精准控制，小于空闲时间的进行阻塞操作。
                    this.waiting(IDLE_INTERVAL);
                } else {
                    // 时间点等待
                    this.waiting(waiting);
                    // 终结检测
                    if (this.context.isShutdown()) {
                        break;
                    }
                    // 获取下一个时间点需要执行的Executor
                    TaskExecutor[] nexts = this.getNextExecutors(nextTime);
                    // 执行每一个Executor
                    int len = nexts.length;
                    for (int i = 0; i < len; i++) {
                        executors.execute(nexts[i]);
                    }
                }
            }
        }
    }

    /**
     * 当收到终结命令时，通知所有任务执行者终结自身
     *
     * @return
     * @throws InterruptedException
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public boolean shutdownAndAwait() throws InterruptedException {
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

    public void init() {
        context.addShutdownHook(new Runnable() {
            public void run() {
                Log.debug("TaskWorker Terminating...");
                try {
                    if (shutdownAndAwait()) {
                        Log.debug("TaskWorker terminated.");
                    } else {
                        Log.debug("TaskWorker terminate was not full.");
                    }
                } catch (InterruptedException e) {
                    Log.debug("TaskWorker terminate field.");
                    Log.error(e.getLocalizedMessage(), e);
                }
            }
        });
    }
}
