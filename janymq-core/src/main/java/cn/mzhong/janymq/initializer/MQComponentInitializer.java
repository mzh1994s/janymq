package cn.mzhong.janymq.initializer;

import cn.mzhong.janymq.core.MQContext;

public interface MQComponentInitializer {

    void init(MQContext context);
}
