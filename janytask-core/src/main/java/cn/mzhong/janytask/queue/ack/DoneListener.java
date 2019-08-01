package cn.mzhong.janytask.queue.ack;

public abstract class DoneListener<R> implements AckListener<R> {

    public void error(Throwable throwable) {

    }
}
