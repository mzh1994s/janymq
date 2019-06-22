package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.line.LineInfo;
import cn.mzhong.janymq.line.LineIDGenerator;

public class RedisKeyGenerator extends LineIDGenerator {

    public RedisKeyGenerator(LineInfo lineInfo) {
        super(lineInfo.getValue(), ":");
        append(lineInfo.getVersion());
    }
}
