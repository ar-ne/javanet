package javanet.c06.t1;

import org.jetbrains.annotations.Nullable;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

//2.	编写一个Java程序，该程序能够利用对称加密算法AES对一段短文进行加密解密。
public class t2 {

    public static final String ALGORITHM = "AES";

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        Key key = genAES();
        byte[] d1 = t1.readAll("TestData/test.txt");
        byte[] d2 = encryptByAESKey(d1, key);
        byte[] d3 = decryptByAESKey(d2, key);
        t1.writeAll("TestData/test.en.txt", d2);
        t1.writeAll("TestData/test.de.txt", d3);
    }

    @Nullable
    public static Key genAES() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(128);
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Key getKey(byte[] encodedKey) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(128);
            return new SecretKeySpec(encodedKey, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encryptByAESKey(byte[] src, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(src);
    }

    private static byte[] decryptByAESKey(byte[] src, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(src);
    }
}
