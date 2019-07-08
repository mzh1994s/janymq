package cn.mzhong.janytask.tool;

/**
 * key生成器
 */
public class IDGenerator {
    private String separator;
    private StringBuilder builder = new StringBuilder();

    public IDGenerator(String node, String separator) {
        builder.append(node);
        this.separator = separator;
    }

    public IDGenerator append(String node) {
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
