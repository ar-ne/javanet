package javanet.l2;

import java.util.concurrent.CountDownLatch;

public class T4 {


    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        CountDownLatch countDownLatchFix = new CountDownLatch(300);
        Counter counter = new Counter();
        CounterFix counterFix = new CounterFix();
        Runnable c = () -> {
            counter.decrement();
            countDownLatch.countDown();
        };
        Runnable cFix = () -> {
            counterFix.decrement();
            countDownLatchFix.countDown();
        };
        for (int i = 0; i < 300; i++) {
            new Thread(c, "original Counter id:" + i).start();
            new Thread(cFix, "Fixed counter id:" + i).start();
        }
        countDownLatch.await();
        countDownLatchFix.await();
    }
}

class Counter {
    private int c = 30;

    public void decrement() {
        if (c > 0)
            c--;
        System.out.println(Thread.currentThread() + "=" + c);
    }
}

class CounterFix {
    private int c = 30;

    public synchronized void decrement() {
        if (c > 0)
            c--;
        System.out.println(Thread.currentThread() + "=" + c);
    }
}