package cn.mzhong.janytask.queue.provider;

import cn.mzhong.janytask.util.StringUtils;

/**
 * 因为需要提供package属性，这里提成公共的Abstract类，其他提供商的实现直接继承此类可节省一个属性的编写
 */
public abstract class AbstractProvider implements QueueProvider {

    private String _package;

    /**
     * packages由{@link #_package} 实时计算而来，执行{@link #setPackage(String)}就会被计算一次
     */
    private String[] packages = new String[0];

    public String[] getPackages() {
        return packages;
    }

    public void setPackage(String _package) {
        this._package = _package;
        if (StringUtils.isEmpty(_package)) {
            packages = new String[0];
        } else {
            String[] packages = _package.split(",|，");
            int index = packages.length;
            while (index-- != 0) {
                packages[index] = packages[index].trim();
            }
            this.packages = packages;
        }
    }
}
