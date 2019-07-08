package cn.mzhong.janytask.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtils {
    final static Logger Log = LoggerFactory.getLogger(ThreadUtils.class);

    private ThreadUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.error("定时器异常", e);
        }
    }
}
