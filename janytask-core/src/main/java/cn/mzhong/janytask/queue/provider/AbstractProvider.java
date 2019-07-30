package cn.mzhong.janytask.queue.provider;

import cn.mzhong.janytask.util.PackageUtils;

/**
 * 因为需要提供package属性，这里提成公共的Abstract类，其他提供商的实现直接继承此类可节省一个属性的编写
 */
public abstract class AbstractProvider implements QueueProvider {
    /**
     * packages由{@link #setPackage} 实时计算而来，执行{@link #setPackage(String)}就会被计算一次
     */
    protected String[] packages = new String[0];

    public String[] getPackages() {
        return packages;
    }

    public void setPackage(String _package) {
        this.packages = PackageUtils.parseMultiple(_package);
    }
}
