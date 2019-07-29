package cn.mzhong.janytask.config;

import java.io.Serializable;

public class ComponentConfig implements Serializable {

    private static final long serialVersionUID = -8720584708083603412L;

    protected String _package;

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }
}
