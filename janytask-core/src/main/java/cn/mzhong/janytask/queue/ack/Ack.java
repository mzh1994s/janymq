package cn.mzhong.janytask.queue.ack;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class Ack<T extends Serializable> implements Serializable {

    public final static Ack BACK = new Ack();
    public final static Ack DONE = new Ack();

    private static final long serialVersionUID = -2512420127598685806L;
    protected T data;

    public Ack() {
    }

    public Ack(T data) {
        this.data = data;
    }

    public Ack<T> listen(AckListener<T> listener) {
        return this;
    }

    public T get() throws InterruptedException, ExecutionException {
        return data;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        return data;
    }

    public static <T extends Serializable> Ack<T> back() {
        return BACK;
    }

    public static <T extends Serializable> Ack<T> done() {
        return DONE;
    }
}
