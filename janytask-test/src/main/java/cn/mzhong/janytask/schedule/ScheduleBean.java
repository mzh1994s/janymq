package cn.mzhong.janytask.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

@Schedule
public class ScheduleBean {

    @Scheduled(cron = "* * * * * ?")
    public void doService() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("doService:" + dateFormat.format(date));
    }

    @Scheduled(cron = "00 01 * * * ?")
    public void doService2() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("doService2:" + dateFormat.format(date));
    }
}
