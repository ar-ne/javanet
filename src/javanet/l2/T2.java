package javanet.l2;

public class T2 {
    public static void x() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + " => " + (i + 1));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(T2::x, "Runnable 方法").start();
        new tx().start();
        Thread.sleep(1000);
    }
}

class tx extends Thread {
    tx() {
        super("继承Thread 方法");
    }

    @Override
    public void run() {
        T2.x();
    }
}