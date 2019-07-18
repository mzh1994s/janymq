package cn.mzhong.janytask.schedule;

import cn.mzhong.janytask.core.TaskComponent;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.executor.TaskExecutor;
import cn.mzhong.janytask.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

public class ScheduleManager implements TaskComponent {

    @SuppressWarnings("unchecked")
    public void init(TaskContext context) {
        String basePackage = context.getApplicationConfig().getBasePackage();
        Set<Class<?>> scheduleClassSet = ClassUtils.scanByAnnotation(basePackage, Schedule.class);
        Iterator<Class<?>> iterator = scheduleClassSet.iterator();
        while (iterator.hasNext()) {
            Class<?> _class = iterator.next();
            Object schedule = createSchedule(_class);
            Method[] methods = _class.getMethods();
            int len = methods.length;
            for (int i = 0; i < len; i++) {
                Method method = methods[i];
                Cron cron = method.getAnnotation(Cron.class);
                if(cron != null){
                    context.getTaskWorker().addExecutor(new ScheduleExecutor(context, schedule));
                }
            }
        }
    }

    protected Object createSchedule(Class<?> _class){
        try {
            return _class.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ScheduleExecutor extends TaskExecutor{

    Object schedule;

    public ScheduleExecutor(TaskContext context, Object schedule) {
        super(context);
        this.schedule = schedule;
    }

    @Override
    protected void execute() {

    }
}
