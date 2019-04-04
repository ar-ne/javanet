/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t2;

public class t1 {
    public static void main(String[] args) {
        Runnable r = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("" + Thread.currentThread().getName() + (i + 1));
            }
        };
//        Thread t = new Thread(r, "T1 ");
//        Thread e1.t2 = new Thread(r, "T2 ");
        xThread x = new xThread("X1");
        xThread x2 = new xThread("X2");

//        t.start();
//        e1.t2.start();
        x.start();
        x2.start();
        try {
//            t.join();
//            e1.t2.join();
            x.join();
            x2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class xThread extends Thread {
    xThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("msg from a class extends Thread " + this.getName() + (i + 1));
        }
    }
}