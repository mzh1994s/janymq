package cn.mzhong.janytask.queue.ack;

public abstract class ErrorListener<R> implements AckListener<R> {
    public void done(R result) {

    }
}
