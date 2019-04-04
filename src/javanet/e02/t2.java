/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.e02;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

public class t2 implements Runnable {
    ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
    ExecutorService service = Executors.newFixedThreadPool(3);
    CyclicBarrier cyclicBarrier = new CyclicBarrier(3, this);

    public static void main(String[] args) {
        t2 t = new t2();
        t.count();
    }

    public void count() {
        for (int ix = 0; ix < 3; ix++) {
            service.execute(() -> {
                for (int i = 0; i < 333; i++) {
                    scores.put(Thread.currentThread().getName() + i, (int) (Math.random() * 80) + 20);
                }
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {
                }
            });
        }
    }

    @Override
    public void run() {
        Iterator<Map.Entry<String, Integer>> iter = scores.entrySet().iterator();
        int x = 0;
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            System.out.println(x++ + ": " + entry.getKey() + " | " + entry.getValue());
        }
    }
}
