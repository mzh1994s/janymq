package cn.mzhong.janytask.exception;

public class NoAnyProviderException extends RuntimeException {

    public NoAnyProviderException() {
        super();
    }

    public NoAnyProviderException(String message) {
        super(message);
    }

    public NoAnyProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAnyProviderException(Throwable cause) {
        super(cause);
    }

    protected NoAnyProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
