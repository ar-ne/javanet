/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c01.t1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class t1 {
    public static void main(String[] args) {
        try {
            File file = new File("test.txt");
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            List<String> list = new ArrayList<>();
            while ((str = bufferedReader.readLine()) != null) {
                list.add(str);
            }
            for (String s : list) {
                if (s.toLowerCase().contains("test"))
                    System.out.println(s);
            }
            bufferedReader.close();
            reader.close();
        } catch (Exception ignored) {
        }
    }
}
