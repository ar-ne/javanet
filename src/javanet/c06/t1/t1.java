package javanet.c06.t1;

import org.jetbrains.annotations.Nullable;

import java.io.*;

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

    @Nullable
    public static byte[] readAll(String fn) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(fn));
            byte[] data = new byte[fileInputStream.available()];
            int readed = fileInputStream.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeAll(String fn, byte[] data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(fn));
            fileOutputStream.write(data);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
