package cn.mzhong.janymq.exception;

public class MQNotFoundException extends RuntimeException {

    public MQNotFoundException(String message) {
        super(message);
    }
}
