package cn.mzhong.janytask.test.main;

import org.springframework.scheduling.support.JanyTask$CronSequenceGenerator;

import java.util.Date;

public class CronTest {
    public static void main(String[] args) {
        JanyTask$CronSequenceGenerator cronSequenceGenerator = new JanyTask$CronSequenceGenerator("* * * * * ?");
        System.out.println(cronSequenceGenerator.next(new Date()));
    }
}
