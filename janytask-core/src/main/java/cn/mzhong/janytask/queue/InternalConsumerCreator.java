package cn.mzhong.janytask.queue;

public class InternalConsumerCreator implements ConsumerCreator {

    public Object createConsumer(Class<?> consumerClass) {
        try {
            return consumerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
