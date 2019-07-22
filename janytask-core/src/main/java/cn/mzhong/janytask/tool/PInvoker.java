package cn.mzhong.janytask.tool;

public interface PInvoker<P> {

    void invoke(P parameter) throws Exception;
}
