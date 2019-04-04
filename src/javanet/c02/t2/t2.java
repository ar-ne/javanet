/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t2;

//2.	编写一个会产生死锁的Java多线程程序，并说明产生死锁的原因
public class t2 {
    public static void main(String[] args) throws InterruptedException {
        final Object r1 = new Object();
        final Object r2 = new Object();
        Runnable x1 = () -> {
            try {
                synchronized (r1) {
                    System.out.println(Thread.currentThread().getName() + " r1 locked,wait 3000ms");
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " Try to lock r2");
                    synchronized (r2) {
                        System.out.println(Thread.currentThread().getName() + " I shell never say this");
                    }
                }
            } catch (Exception ignored) {
            }
        };
        Runnable x2 = () -> {
            try {
                synchronized (r2) {
                    System.out.println(Thread.currentThread().getName() + " r2 locked,wait 3000ms");
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + " Trying to lock r1");
                    synchronized (r1) {
                        System.out.println(Thread.currentThread().getName() + "I'm gonna say nothing!");
                    }
                }
            } catch (Exception ignored) {
            }
        };
        Thread t1 = new Thread(x1, "Thread x1:");
        Thread t2 = new Thread(x2, "Thread x2:");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
