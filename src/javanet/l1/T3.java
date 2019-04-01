package javanet.l1;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class T3 {
    public static void main(String[] args) {
        try {
            copyDir("Z:\\Lab\\L1\\src", "Z:\\Lab\\L1\\scr");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyDir(String fnp1, String fnp2) throws IOException {
        File fx1 = new File(fnp1);
        File fx2 = new File(fnp2);
        File[] l1 = Objects.requireNonNull(fx1.listFiles());
        List<File> l2 = Arrays.asList(Objects.requireNonNull(fx2.listFiles()));
        for (File f : l1) {
            if (f.isDirectory()) continue;
            for (int i = 0; i < l2.size(); i++) {
                if (l2.get(i).getName().equals(f.getName())) continue;
            }
            T2.copy(f.getAbsolutePath(), fnp2 + "\\" + f.getName());
        }
    }
}
