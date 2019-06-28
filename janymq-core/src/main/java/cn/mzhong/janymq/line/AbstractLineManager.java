package cn.mzhong.janymq.line;

import cn.mzhong.janymq.core.MQContext;

public abstract class AbstractLineManager implements LineManager {
    protected String ID;
    protected MQContext context;
    protected DataSerializer dataSerializer;
    protected LineInfo lineInfo;

    @Override
    public String ID() {
        return this.ID;
    }

    public AbstractLineManager(MQContext context, LineInfo lineInfo) {
        this.context = context;
        this.dataSerializer = context.getDataSerializer();
        this.lineInfo = lineInfo;
        this.ID = lineInfo.ID();
    }
}
