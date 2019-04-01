package javanet.l02;

import java.io.File;
import java.io.IOException;

public class T3 {
    public static void main(String[] args) {
        File file = new File("Test.txt");
        for (int i = 0; i < 3; i++) {
            new Copyx(file.getAbsolutePath(), file.getAbsolutePath() + i);
        }
    }

    static class Copyx extends Thread {
        String x1, x2;

        Copyx(String x1, String x2) {
            super();
            this.x1 = x1;
            this.x2 = x2;
            start();
        }

        @Override
        public void run() {
            try {
                l1.T2.copy(x1, x2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
