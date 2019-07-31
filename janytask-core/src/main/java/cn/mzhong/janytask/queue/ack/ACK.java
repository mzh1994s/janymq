package cn.mzhong.janytask.queue.ack;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ACK implements Future<Serializable>, Serializable {

    private static final long serialVersionUID = -2512420127598685806L;
    protected Serializable object;

    public ACK(Serializable object) {
        this.object = object;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    public Serializable get() throws InterruptedException, ExecutionException {
        return object;
    }

    public Serializable get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return object;
    }
}
