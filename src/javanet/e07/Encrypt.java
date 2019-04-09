package javanet.e07;

import java.io.*;

public class Encrypt {
    public static void main(String[] args) throws Exception {
        new File("in.txt").createNewFile();
        new File("out.txt").createNewFile();
        new File("dec.txt").createNewFile();
        enc("in.txt", "out.txt");
        enc("out.txt", "dec.txt");

    }

    static void enc(String inf, String outf) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(new File(inf)));
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(outf)));

        String line;
        while ((line = in.readLine()) != null) {
            out.write(new String(ende(line, "zsnb")));
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
}
