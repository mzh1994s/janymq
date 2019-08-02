package cn.mzhong.janytask.queue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = -2043879152912282934L;

    protected String id;
    protected String queueId;
    protected Status status;
    protected Date createTime;
    protected Date pushTime;
    protected Date doneTime;
    protected Date errorTime;
    protected Throwable throwable;
    protected Object[] args;
    protected Object result;

    public Message() {
        // 生成22位key 就像：31814797796271-R795801
        id = System.currentTimeMillis() + "-R" + Math.round((Math.random() * 9 + 1) * 1000000);
        createTime = new Date();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
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
                ", args=" + Arrays.toString(args) +
                '}';
    }

    public static enum Status {
        // 等待状态
        Wait("W"),
        // 锁定状态
        Lock("L"),
        // 完成状态
        Done("D"),
        // 错误状态
        Error("E");

        public final String value;

        Status(String value) {
            this.value = value;
        }

        public static Status get(String value) {
            Status[] values = Status.values();
            int index = Status.values().length;
            while (index-- > 0) {
                Status status = values[index];
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return null;
        }

    }
}


