package cn.mzhong.janytask.queue;

public interface InstanceCreator<T> {

    T create(Class<T> _class);
}
