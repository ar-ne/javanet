package javanet.l2;

import java.util.ArrayList;
import java.util.Scanner;

public class T1 {
    public static void main(String[] args) throws InterruptedException {
        Runnable w = () -> {
            try {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Scanner scanner = new Scanner(System.in);
        int t = scanner.nextInt();
        ArrayList<Thread> xx = new ArrayList<>();
        while (t != 0) {
            xx.add(new Thread(w, "" + t--));
            xx.get(xx.size() - 1).start();
        }
        for (Thread xx1 : xx) {
            xx1.join();
        }
    }
}
