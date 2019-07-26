package cn.mzhong.janytask.core;

import cn.mzhong.janytask.serialize.JdkDataSerializer;
import cn.mzhong.janytask.serialize.Serializer;

/**
 * 上下文空间可以理解为一个公共储存对象的区域，存放着一个公共的组件、配置、标志等，
 */
public abstract class TaskContext {
    /**
     * 全局的序列化接口
     */
    protected Serializer serializer = new JdkDataSerializer();
    /**
     * 安全退出ShutdownHook
     *
     * @since 2.0.0
     */
    final protected TaskShutdownHook shutdownHook = new TaskShutdownHook(this);

    /**
     * 应用程序终结标志
     *
     * @since 1.0.0
     */
    protected volatile boolean shutdown = false;

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public TaskShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addShutdownHook(Runnable runnable) {
        shutdownHook.add(runnable);
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
