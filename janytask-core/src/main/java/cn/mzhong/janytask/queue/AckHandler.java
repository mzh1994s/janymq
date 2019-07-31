package cn.mzhong.janytask.queue;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * 应答句柄
 */
public interface AckHandler<T extends Serializable> {
    /**
     * 异步调用，从Future中取结果值
     *
     * @return
     */
    Future<T> async();

    /**
     * 同步调用，直接取结果值
     *
     * @return
     */
    T sync();
}
