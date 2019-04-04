/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t1;

//1.	编写一个Java程序，该程序将启动4个线程，其中3个是掷硬币线程，1个是主线程；每个掷硬币线程将连续掷出20次硬币，如果出现3次以上的正面便将其打印出来。
public class t1 {
    public static void main(String[] args) {
        Runnable r = () -> {
            int count = 0;
            for (int i = 0; i < 20; i++) {
                if (Math.random() >= 0.5f) {
                    count++;
                }
                if (count == 3)
                    System.out.println("!!!" + Thread.currentThread().getName() + " got 3 face,count=" + count++);
            }
        };
        Thread[] ts = new Thread[3];
        for (int i = 0; i < 3; i++) {
            ts[i] = new Thread(r, "Thread:" + i);
            ts[i].start();
        }
        for (int i = 0; i < 3; i++) {
            try {
                if (ts[i].isAlive()) ts[i].join();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
