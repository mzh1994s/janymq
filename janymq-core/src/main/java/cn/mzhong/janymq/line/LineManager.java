package cn.mzhong.janymq.line;

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
     * 获取一个消息
     *
     * @return
     */
    Message poll();

    /**
     * 归还一个消息
     * @param message
     */
    void back(Message message);

    /**
     * 消息被消费完成
     *
     * @param message
     */
    void done(Message message);

    /**
     * 消息消费出错
     *
     * @param message
     */
    void error(Message message);

    /**
     * 返回任务列表的长度
     *
     * @return
     */
    long length();
}
