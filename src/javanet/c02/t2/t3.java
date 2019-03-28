/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t2;

//3.	利用线程通知机制编写一个Java多线程程序，模拟商店对某件商品的进货与销售过程并将相关信息打印出来，具体要求如下：
//        进货与销售过程各由一个线程模拟；当商品数目少于10时进货，进货数目随机生成但不少于50；销售数目随机生成，数目不大于商品数量；2次销售之间的时间随机生成，但不大于2s。
public class t3 {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        final Integer[] 货 = {1};
        Runnable 进货 = () -> {
            try {
                synchronized (货) {
                    while (true) {
                        货.wait();
                        货[0] += (int) (Math.random() * 50) + 51;
                        System.out.println("进货之后还有：" + 货[0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Runnable 卖货 = () -> {
            try {
                while (true) {
                    synchronized (货) {
                        货[0] -= (int) (Math.random() * (货[0] - 1) / 2 + 1);
                        System.out.println("卖货之后还剩：" + 货[0]);
                        if (货[0] < 10) 货.notify();
                    }
                    Thread.sleep((int) (Math.random() * 2000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread t1 = new Thread(进货);
        Thread t2 = new Thread(卖货);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
