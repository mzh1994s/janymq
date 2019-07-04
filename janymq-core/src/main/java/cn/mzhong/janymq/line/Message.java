package cn.mzhong.janymq.line;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Message implements Serializable {
    private static final long serialVersionUID = -2043879152912282934L;
    protected String key;
    protected String lineID;
    protected Date pushTime;
    protected Date doneTime;
    protected Date errorTime;
    protected Throwable throwable;
    protected Object[] content;

    public Message() {
        // 生成22位key 就像：31814797796271-R795801
        key = System.currentTimeMillis() + "-R" + Math.round((Math.random() * 9 + 1) * 1000000);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
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

    public Object[] getContent() {
        return content;
    }

    public void setContent(Object[] content) {
        this.content = content;
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
                ", lineID='" + lineID + '\'' +
                ", pushTime=" + pushTime +
                ", doneTime=" + doneTime +
                ", errorTime=" + errorTime +
                ", throwable=" + throwable +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
