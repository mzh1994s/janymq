package cn.mzhong.janytask.queue;

public class NoSuchProducerException extends RuntimeException {

    public NoSuchProducerException(String message) {
        super(message);
    }
}
