package cn.mzhong.janytask.queue.provider.jdbc.mapper;

import cn.mzhong.janytask.queue.provider.jdbc.BytesMessage;

import java.util.LinkedList;

public interface MessageMapper {

    /**
     * 初始化
     */
    void init();

    /**
     * 检查表是否存在
     *
     * @return
     */
    boolean isTableExists();

    /**
     * 创建表
     */
    void createTable();

    /**
     * 保存消息到列表
     *
     * @param message
     */
    void save(BytesMessage message);

    /**
     * 返回wait状态下的key，一般情况下返回所有，具体返回数量由实现方决定
     *
     * @return
     */
    LinkedList<String> keys();

    /**
     * 将一个key锁定
     *
     * @param key
     * @return 成功锁定key返回true，如果key被其他消费者锁定将锁定失败，返回false
     */
    boolean lock(String key);

    /**
     * 解锁Key
     *
     * @param key
     * @return
     */
    boolean unLock(String key);

    /**
     * 获取一个消息的数据
     *
     * @param key
     * @return
     */
    BytesMessage get(String key);

    /**
     * 将一个消息设置为完成状态
     *
     * @param message
     */
    void done(BytesMessage message);

    /**
     * 将一个消息设置为错误状态
     *
     * @param message
     */
    void error(BytesMessage message);

    /**
     * 返回指定LineID的等待执行的消息数量
     *
     * @param lineID
     * @return
     */
    long length(String lineID);
}
