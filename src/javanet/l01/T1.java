package javanet.l01;

import java.io.FileInputStream;
import java.io.IOException;

public class T1 {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("Test.txt");
            int b, n = 0;
            while ((b = fis.read()) != -1) {
                System.out.print(" " + Integer.toHexString(b));
                if (((++n) % 10) == 0) System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
