package cn.mzhong.janytask.test.main;

public class ShutdownHookTest {
    public static void main(String[] args) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("结束中...");
            }
        }));

        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("前");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("后");
                }
            }
        });
        thread.start();
    }
}
