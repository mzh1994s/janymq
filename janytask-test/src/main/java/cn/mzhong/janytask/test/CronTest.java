package cn.mzhong.janytask.test;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.Date;

public class CronTest {
    public static void main(String[] args) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator("0 */5 * * * ?");
        System.out.println(cronSequenceGenerator.next(new Date()));
    }
}
