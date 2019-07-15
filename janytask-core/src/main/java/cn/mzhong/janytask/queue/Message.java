package cn.mzhong.janytask.queue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Message implements Serializable {
    private static final long serialVersionUID = -2043879152912282934L;

    // 等待状态
    public final static String STATUS_WAIT = "W";
    // 锁定状态
    public final static String STATUS_LOCK = "L";
    // 完成状态
    public final static String STATUS_DONE = "D";
    // 错误状态
    public final static String STATUS_ERROR = "E";

    protected String id;
    protected String queueId;
    protected String status;
    protected Date pushTime;
    protected Date doneTime;
    protected Date errorTime;
    protected Throwable throwable;
    protected Object[] content;

    public Message() {
        // 生成22位key 就像：31814797796271-R795801
        id = System.currentTimeMillis() + "-R" + Math.round((Math.random() * 9 + 1) * 1000000);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

        return id != null ? id.equals(message.id) : message.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", queueId='" + queueId + '\'' +
                ", status='" + status + '\'' +
                ", pushTime=" + pushTime +
                ", doneTime=" + doneTime +
                ", errorTime=" + errorTime +
                ", throwable=" + throwable +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
