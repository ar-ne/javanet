package javanet.l1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class T2 {
    public static void main(String[] args) {
        try {
            copy("Test.txt", "Test2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String fnp1, String fnp2) throws IOException {
        File fx1 = new File(fnp1);
        File fx2 = new File(fnp2);
        if (!fx1.exists()) {
            System.out.println("f1 not exists");
            return;
        }
        if (chkFile(fx2, false)) {
            FileInputStream in = new FileInputStream(fx1);
            FileOutputStream out = new FileOutputStream(fx2);
            byte[] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }
            in.close();
            out.flush();
            out.close();
        } else {
            System.out.println("Skipped:");
            System.out.println(fnp1);
            System.out.println(fnp2);
            System.out.println();
        }
    }

    public static boolean chkFile(File f) throws IOException {
        return chkFile(f, true);
    }

    private static boolean chkFile(File f, boolean overwrite) throws IOException {
        if (f.exists())
            if (overwrite) {
                f.delete();
                return f.createNewFile();
            } else {
                return false;
            }
        return f.createNewFile();
    }
}
