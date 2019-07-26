package cn.mzhong.janytask.test;

import cn.mzhong.janytask.queue.Consumer;
import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.schedule.Schedule;
import cn.mzhong.janytask.tool.AnnotationPatternClassScanner;

public class ScannerTest {
    public static void main(String[] args) {
        AnnotationPatternClassScanner scanner = new AnnotationPatternClassScanner();
        scanner.addAnnotation(Consumer.class, Producer.class, Schedule.class);
        scanner.addPackages("org.springframework", "org");
        System.out.println(scanner.scan());
    }
}
