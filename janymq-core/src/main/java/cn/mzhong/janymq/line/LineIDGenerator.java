package cn.mzhong.janymq.line;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;

/**
 * key生成器
 */
public class LineIDGenerator {
    private String separator;
    private StringBuilder builder = new StringBuilder();

    public LineIDGenerator(String node, String separator) {
        builder.append(node);
        this.separator = separator;
    }

    public LineIDGenerator append(String node) {
        if (node != null && node.trim().length() != 0) {
            builder.append(separator).append(node);
        }
        return this;
    }

    public String generate() {
        return builder.toString();
    }

    @Override
    public String toString() {
        return generate();
    }
}
