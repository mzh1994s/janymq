package cn.mzhong.janytask.application;

/**
 * 内部Context，让Abstract的TaskContext创建内部类，作用不大
 *
 * @since 2.0.0
 */
class InternalContext extends TaskContext {

    public InternalContext(Application application) {
        super(application);
    }
}
