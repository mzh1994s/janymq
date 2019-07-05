package cn.mzhong.janymq.util;

public interface PRInvoker<P, R> {

    R invoke(P parameter) throws Exception;
}
