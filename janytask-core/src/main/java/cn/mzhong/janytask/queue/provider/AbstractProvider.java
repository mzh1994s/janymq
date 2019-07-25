package cn.mzhong.janytask.queue.provider;

/**
 * 因为需要提供package属性，这里提成公共的Abstract类，其他提供商的实现直接继承此类可节省一个属性的编写
 */
public abstract class AbstractProvider implements QueueProvider {

    protected String _package;

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }
}
