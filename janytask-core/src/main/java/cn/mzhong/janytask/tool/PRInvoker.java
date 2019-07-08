package cn.mzhong.janytask.tool;

public interface PRInvoker<P, R> {

    R invoke(P parameter) throws Exception;
}
