package cn.mzhong.janytask.schedule;

import cn.mzhong.janytask.core.TaskComponent;
import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.core.TaskExecutor;
import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

public class ScheduleManager implements TaskComponent {

    Logger Log = LoggerFactory.getLogger(ScheduleManager.class);

    protected TaskContext context;

    public void setContext(TaskContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        String basePackage = context.getApplicationConfig().getName();
        Set<Class<?>> scheduleClassSet = ClassUtils.scanByAnnotation(basePackage, Schedule.class);
        Iterator<Class<?>> iterator = scheduleClassSet.iterator();
        while (iterator.hasNext()) {
            Class<?> _class = iterator.next();
            Object schedule = createSchedule(_class);
            Method[] methods = _class.getMethods();
            int len = methods.length;
            for (int i = 0; i < len; i++) {
                Method method = methods[i];
                Scheduled scheduled = method.getAnnotation(Scheduled.class);
                if (scheduled != null) {
                    JanyTask$CronSequenceGenerator cronSequenceGenerator = new JanyTask$CronSequenceGenerator(scheduled.cron(), scheduled.zone());
                    context.getTaskWorker().addExecutor(new ScheduleExecutor(context, schedule, method, cronSequenceGenerator));
                }
            }
        }
    }

    protected Object createSchedule(Class<?> _class) {
        try {
            return _class.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    class ScheduleExecutor extends TaskExecutor {

        Object schedule;
        Method scheduled;

        public ScheduleExecutor(TaskContext context, Object schedule, Method scheduled, JanyTask$CronSequenceGenerator cronSequenceGenerator) {
            super(context, cronSequenceGenerator);
            this.schedule = schedule;
            this.scheduled = scheduled;
        }

        @Override
        protected void execute() {
            try {
                scheduled.invoke(schedule);
            } catch (Exception e) {
                Log.error(e.getLocalizedMessage(), e);
            }
        }
    }
}

