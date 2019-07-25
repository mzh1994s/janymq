package cn.mzhong.janytask.queue.provider;

public class DuplicateProviderException extends RuntimeException {
    public DuplicateProviderException() {
        super();
    }

    public DuplicateProviderException(String message) {
        super(message);
    }

    public DuplicateProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateProviderException(Throwable cause) {
        super(cause);
    }

    protected DuplicateProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
