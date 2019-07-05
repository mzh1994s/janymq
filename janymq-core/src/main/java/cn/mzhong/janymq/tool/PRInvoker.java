package cn.mzhong.janymq.tool;

public interface PRInvoker<P, R> {

    R invoke(P parameter) throws Exception;
}
