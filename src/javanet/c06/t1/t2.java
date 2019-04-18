package javanet.c06.t1;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

//2.	编写一个Java程序，该程序能够利用对称加密算法AES对一段短文进行加密解密。
public class t2 {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        Key key = new SecretKeySpec(generator.generateKey().getEncoded(), "AES");
        List<String> list = t1.readFile("TestData/test.txt");
        List<byte[]> enL = en(list, key);
        list.clear();
        for (byte[] bytes : enL)
            list.add(new String(bytes));
        t1.writeFile("test.en.txt", list);

        List<String> deL = de(enL, key);
        list.clear();
        t1.writeFile("test.de.txt", deL);
    }

    private static List<byte[]> en(List<String> list, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        ArrayList<byte[]> arrayList = new ArrayList<>();
        for (String s : list)
            arrayList.add(cipher.doFinal(s.getBytes()));
        return arrayList;
    }

    private static List<String> de(List<byte[]> list, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        ArrayList<String> arrayList = new ArrayList<>();
        for (byte[] bytes : list)
            arrayList.add(new String(cipher.doFinal(bytes)));
        return arrayList;
    }
}
