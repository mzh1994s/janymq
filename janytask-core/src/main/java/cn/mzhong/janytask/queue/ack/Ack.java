package cn.mzhong.janytask.queue.ack;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Ack<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -2512420127598685806L;
    protected T data;

    public Ack<T> addListener(AckListener<T> listener) {
        return this;
    }

    public Ack<T> push() {
        return this;
    }

    public Ack(T data) {
        this.data = data;
    }

    public T get() throws InterruptedException, ExecutionException {
        return data;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        return data;
    }
}
