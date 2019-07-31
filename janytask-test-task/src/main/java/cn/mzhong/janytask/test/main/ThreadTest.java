package cn.mzhong.janytask.test.main;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadTest {
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Object> submit = executorService.submit(new Callable<Object>() {
            public Object call() throws Exception {
                Thread.sleep(4000);
                return "2343";
            }
        });
        System.out.println(submit.get());
    }
}
