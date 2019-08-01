package cn.mzhong.janytask.queue.ack;

public interface AckListener<R> {

    void done(R result);

    void error(Throwable throwable);

}
