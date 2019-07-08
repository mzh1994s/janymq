package cn.mzhong.janytask.queue;

public interface LineManager {

    /**
     * 获取lineID
     *
     * @return
     */
    String ID();

    /**
     * 推送消息
     *
     * @param message
     */
    void push(Message message);

    /**
     * 获取一个消息<br/>
     * 获取消息同时应当对消息加锁，保证不被其他消费者多次消费
     *
     * @return
     */
    Message poll();

    /**
     * 归还一个消息<br/>
     * 将一个消息还原为等待状态，可以被再次消费，一般loopline会用到此功能
     *
     * @param message
     */
    void back(Message message);

    /**
     * 消息被消费完成<br/>
     * 将消息从等待列表转存到完成列表，不要忘记删除该消息的锁，以免浪费存储空间。
     *
     * @param message
     */
    void done(Message message);

    /**
     * 消息消费出错<br/>
     * 将消息从等待列表转存到错误列表，不要忘记删除该消息的锁，以免浪费存储空间。<br/>
     * 错误列表应由运维处理
     *
     * @param message
     */
    void error(Message message);

    /**
     * 返回等待任务列表的长度
     *
     * @return
     */
    long length();
}
