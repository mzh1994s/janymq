package cn.mzhong.janymq.line;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = -2043879152912282934L;
    protected String key;
    protected Date pushTime;
    protected Date doneTime;
    protected Date errorTime;
    protected Throwable throwable;
    protected Object data;

    public Message() {
        key = System.nanoTime() + "-R" + Math.round(Math.random() * 10000);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public Date getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(Date errorTime) {
        this.errorTime = errorTime;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(key, message.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Message{" +
                "key='" + key + '\'' +
                ", pushTime=" + pushTime +
                ", doneTime=" + doneTime +
                ", errorTime=" + errorTime +
                ", throwable=" + throwable +
                ", data=" + data +
                '}';
    }
}
