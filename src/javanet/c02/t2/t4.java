/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t2;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

//4.	设现有customCount人抢购20件商品（每件商品的价格20-100元不等），要求如下：
//        每人购买的商品不能够超过3件；购买的商品价格总额不能够超过100元；抢购时不知商品价格，结账时才知道商品价格；如果超过限额将退回部分商品，退回策略自定，退回后可再次抢购。
//        请编写一个Java多线程程序，模拟并打印出抢购过程。
public class t4 implements Runnable {
    static final int customCount = 50;
    static int totalSold = 0;
    static CyclicBarrier perpareBarrier = new CyclicBarrier(customCount, new t4());

    public static void main(String[] args) throws InterruptedException {
        商品[] s = new 商品[20];


        int[] customerID = new int[customCount];
        for (int i = 0; i < customerID.length; i++) {
            customerID[i] = -1;
        }
        for (int i = 0; i < s.length; i++) {
            s[i] = new 商品();
            System.out.println(s[i]);
        }
        Runnable 顾客 = () -> {
            AtomicInteger bought = new AtomicInteger();
            int id = -1;
            final int[] bal = {100};
            Map<Integer, 商品> cart = new HashMap<>();
            Map<Integer, 商品> boughts = new HashMap<>();
            Runnable 抢购 = () -> {
                int locked = cart.size();
                synchronized (s) {
                    for (int i = 0; locked < 3 && i < s.length; i++) {
                        if (s[i].available && s[i].lock()) {
                            cart.put(i, s[i]);
                            locked++;
                            cat(Thread.currentThread().getName(), i);
                        }
                    }
                }
            };
            Runnable 结账 = () -> {
                if (cart.size() == 0) {
                    System.out.println(Thread.currentThread().getName() + "未抢到，等待下一轮");
                    return;
                }
                int totalPrice = 0;
                for (Map.Entry<Integer, 商品> entry1 : cart.entrySet()) {
                    totalPrice += entry1.getValue().price;
                }
                if (totalPrice > bal[0]) {
                    //价格大于100则从map前部删去
                    List<Integer> keyList = new ArrayList<>();
                    for (Map.Entry<Integer, 商品> entry : cart.entrySet()) {
                        if (totalPrice - entry.getValue().price <= bal[0]) {
                            keyList.add(entry.getKey());
                            synchronized (s) {
                                s[entry.getKey()].available = true;
                            }

                            break;
                        }
                        totalPrice -= entry.getValue().price;
                        keyList.add(entry.getKey());
                        synchronized (s) {
                            s[entry.getKey()].available = true;
                        }
                    }
                    for (Integer integer : keyList) {
                        cart.remove(integer);
                    }
                    if (cart.size() == 0) return;
                }
                for (Map.Entry<Integer, 商品> entry : cart.entrySet()) {
                    synchronized (s) {
                        bal[0] -= entry.getValue().price;
                        s[entry.getKey()].buy();
                        sold(Thread.currentThread().getName(), entry.getKey());
                        bought.getAndIncrement();
                        boughts.put(entry.getKey(), entry.getValue());
                    }
                }
                System.out.println(Thread.currentThread().getName() + " 抢到了 " + cart.size() + " 个，总价 " + totalPrice);
            };
            //分配ID
            synchronized (customerID) {
                for (int i = 0; i < customerID.length; i++) {
                    if (customerID[i] == -1) {
                        id = i;
                        customerID[i] = i;
                        break;
                    }
                }
            }
            try {
                perpareBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
            while (customerID[id] != -1 || bought.get() == 3) {
                抢购.run();
                try {
                    perpareBarrier.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {
                }
                结账.run();
                try {
                    perpareBarrier.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {
                }
                cart.clear();

            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(Thread.currentThread().getName()).append("Finished work=>Total bought: ").append(boughts.size()).append(" | { ");
            for (Map.Entry<Integer, 商品> entry : boughts.entrySet()) {
                stringBuffer.append(entry.getKey()).append(" ");
            }
            stringBuffer.append("}");
            System.out.println(stringBuffer.toString());
        };
        Thread[] customs = new Thread[customCount];
        for (int i = 0; i < customCount; i++) {
            customs[i] = new Thread(顾客, "Customer " + i + " ");
            customs[i].start();
        }
        while (true) {
            int totalSolds = (int) Arrays.stream(s).filter(商品 -> 商品.sold).count();
            System.out.println("                                                   ***总计售出：" + totalSolds);
            if (totalSolds == 20) break;
            Thread.sleep(100);
        }
        for (int j = 0; j < customerID.length; j++) {
            customerID[j] = -1;
        }
        for (Thread custom : customs) {
            try {
                custom.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sold(String threadName, int index) {
        totalSold++;
        System.out.println(threadName + " 购买商品 ID: " + index);
    }

    public static void cat(String threadName, int index) {
        System.out.println(threadName + " 锁定商品 ID: " + index);
    }

    @Override
    public void run() {
        System.out.println("All reached barrier");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        perpareBarrier.reset();
    }

    static class 商品 {
        boolean available = true;
        boolean sold = false;
        int price = (int) (Math.random() * 80) + 20;

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public int getPrice() {
            return price;
        }

        /**
         * @return true if lock successful
         */
        public synchronized boolean lock() {
            if (available) {
                available = false;
                return true;
            }
            return false;
        }

        /**
         * @return true if sold successful
         */
        public synchronized boolean buy() {
            if (!sold) {
                available = false;
                sold = true;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return super.toString() + "{" +
                    "available=" + available +
                    ", price=" + price +
                    '}';
        }
    }
}
