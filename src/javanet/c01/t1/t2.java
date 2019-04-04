/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class t2 {
    public static void main(String[] args) {
        try {
            File file = new File("random.txt");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else file.createNewFile();
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (int i = 0; i < 10; i++) {
                String str = String.format("Number %s: %s", i, (int) (Math.random() * 99 + 1));
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            writer.close();
        } catch (Exception ignored) {
        }
    }
}
