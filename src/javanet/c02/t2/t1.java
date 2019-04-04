/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t2;

//1.	利用线程同步机制编写一个Java多线程程序，模拟100个球迷抢购20张门票的过程。
public class t1 {
    public static void main(String[] args) {
        final Integer[] 票 = {20};
        Runnable 抢票 = () -> {
            synchronized (票[0]) {
                if (票[0] > 0)
                    System.out.println(Thread.currentThread().getName() + "抢到了票：" + 票[0]--);
                else System.out.println(Thread.currentThread().getName() + "没抢到票");
            }
        };
        for (int i = 0; i < 100; i++) {
            Thread 球迷x = new Thread(抢票, "球迷" + i);
            球迷x.start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
