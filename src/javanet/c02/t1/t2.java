/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t1;

//2.	编写一个Java程序，该程序将启动4个线程，其中3个是掷硬币线程，1个是主线程。每个掷硬币线程将连续掷出若干次硬币（10次以内，次数随机生成）；主线程将打印出正面出现的总次数以及正面出现的概率。
public class t2 {
    public static void main(String[] args) {
        Thread[] ts = new Thread[3];
        x[] xes = new x[3];
        for (int i = 0; i < 3; i++) {
            xes[i] = new x();
            ts[i] = new Thread(xes[i], "Thread:" + i);
            ts[i].start();
        }
        for (int i = 0; i < 3; i++) {
            try {
                if (ts[i].isAlive()) ts[i].join();
            } catch (InterruptedException ignored) {
            }
        }
        for (int i = 0; i < 3; i++) {
            System.out.print("Thread:" + i);
            System.out.println("总数：" + xes[i].t + " 正面概率：" + (1.0 * xes[i].count / xes[i].t));
        }
    }

    static class x implements Runnable {
        int count = 0;
        int t = (int) (Math.random() * 9) + 1;

        @Override
        public void run() {
            for (int i = 0; i < t; i++) {
                if (Math.random() >= 0.5f) {
                    count++;
                }
            }
        }
    }
}
