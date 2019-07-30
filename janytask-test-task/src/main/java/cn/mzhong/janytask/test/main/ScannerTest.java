package cn.mzhong.janytask.test.main;

import cn.mzhong.janytask.queue.Consumer;
import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.schedule.Schedule;
import cn.mzhong.janytask.tool.AnnotationPatternClassScanner;

import java.lang.annotation.Annotation;

public class ScannerTest {
    public static void main(String[] args) throws ClassNotFoundException {
        AnnotationPatternClassScanner scanner = new AnnotationPatternClassScanner();
        scanner.addAnnotation(Consumer.class, Producer.class, Schedule.class);
        scanner.addPackages("cn.mzhong.janytask.tproducer", "cn.mzhong.janytask.tconsumer", "org.mzhong.janytask.tproducer");
        scanner.scan();
        System.out.println(scanner.select(new String[]{"cn.mzhong"}, new Class[]{Consumer.class}));
    }
}
