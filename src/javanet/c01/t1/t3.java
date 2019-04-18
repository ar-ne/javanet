/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t1;

import java.io.*;

public class t3 {
    public static void main(String[] args) {
        try {
            File inFIle = new File("TestData/random.txt");
            File outFIle = new File("TestData/test.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(outFIle, true));
            BufferedReader in = new BufferedReader(new FileReader(inFIle));
            String line;
            while ((line = in.readLine()) != null) {
                int x = Integer.parseInt(line.substring(line.indexOf(": ") + 2));
                if (x > 50) {
                    out.newLine();
                    out.write(line);
                }
            }
            out.flush();
            in.close();
            out.close();
        } catch (Exception ignored) {
        }
    }
}
