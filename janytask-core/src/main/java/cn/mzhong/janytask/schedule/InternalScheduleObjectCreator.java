package cn.mzhong.janytask.schedule;

class InternalScheduleObjectCreator implements ScheduleObjectCreator {

    public Object createScheduleObject(Class<?> _class) {
        try {
            return _class.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
