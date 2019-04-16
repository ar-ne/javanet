package javanet.c06.t1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//1.	自行设计一个简单的对称加密算法，并利用该算法对一篇短文进行加密解密。
public class t1 {
    static final String pathPerfix = "src/javanet/c06/t1/";

    public static void main(String[] args) throws Exception {
        enc("c06t11.txt", "c06t12.txt");
        enc("c06t12.txt", "c06t13.txt");

    }

    static void enc(String inf, String outf) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(new File(inf)));
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(outf)));

        String line;
        while ((line = in.readLine()) != null) {
            out.write(new String(ende(line, "1234")));
        }

        in.close();
        out.flush();
        out.close();
    }

    static char[] ende(String str, String e) {
        char[] c = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            c[i] = (char) (str.charAt(i) ^ e.charAt(i % e.length()));
        }
        return c;
    }

    static List<String> readFile(String fn) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(fn)));
        List<String> list = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }
        reader.close();
        return list;
    }

    static void writeFile(String fn, List<String> con) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fn)));
        for (String s : con) {
            writer.write(s);
            writer.newLine();
        }
        writer.close();
    }
}
