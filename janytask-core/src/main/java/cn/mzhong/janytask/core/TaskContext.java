package cn.mzhong.janytask.core;

/**
 * 上下文空间可以理解为一个公共储存对象的区域，存放着一个公共的组件、配置、标志等，
 */
public abstract class TaskContext {
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
