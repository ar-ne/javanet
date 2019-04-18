package javanet.c06.t1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import static javax.crypto.Cipher.*;

//3.	编写一个Java程序，该程序能够利用非对称加密算法RSA对一段短文进行加密解密。
public class t3 {

    public static final String ALGORITHM = "RSA";

    public static void main(String[] args) throws IOException {
        KeyPair keyPair = genRSA();
        if (keyPair != null) {
            byte[] original = t1.readAll("TestData/test.txt");
            byte[] encrypted = encryptByPublicKey(original, encodeKey(keyPair.getPublic()));
            byte[] decrypted = decryptByPrivateKey(encrypted, encodeKey(keyPair.getPrivate()));

//            byte[] encrypted = encryptByPrivateKey(original, encodeKey(keyPair.getPrivate()));
//            byte[] decrypted = decryptByPublicKey(encrypted, encodeKey(keyPair.getPublic()));

            System.out.println("===================original=========================");
            System.out.println(new String(original));
            System.out.println("\n\n\n");
            System.out.println("===================encrypted=========================");
            System.out.println(new String(encrypted));
            System.out.println("\n\n\n");
            System.out.println("===================decrypted=========================");
            System.out.println(new String(decrypted));
            System.out.println("\n\n\n");
        }
    }

    @Nullable
    public static byte[] doFinal(@NotNull Cipher cipher, byte[] data) {
        try {
            return cipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Cipher getCipher(byte[] encodedKey, int keyType, int workMode) {
        Cipher cipher = null;
        try {
            if (keyType == PUBLIC_KEY) {
                KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
                cipher = Cipher.getInstance(factory.getAlgorithm());
                cipher.init(workMode, factory.generatePublic(new X509EncodedKeySpec(encodedKey)));
            }
            if (keyType == PRIVATE_KEY) {
                KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
                cipher = Cipher.getInstance(factory.getAlgorithm());
                cipher.init(workMode, factory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey)));
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    public static byte[] encodeKey(@NotNull Key key) {
        return key.getEncoded();
    }


    public static byte[] encryptByPrivateKey(byte[] bytes, byte[] key) {
        return doFinal(Objects.requireNonNull(getCipher(key, PRIVATE_KEY, ENCRYPT_MODE)), bytes);
    }

    public static byte[] decryptByPrivateKey(byte[] bytes, byte[] key) {
        return doFinal(Objects.requireNonNull(getCipher(key, PRIVATE_KEY, DECRYPT_MODE)), bytes);

    }

    public static byte[] encryptByPublicKey(byte[] bytes, byte[] key) {
        return doFinal(Objects.requireNonNull(getCipher(key, PUBLIC_KEY, ENCRYPT_MODE)), bytes);
    }

    public static byte[] decryptByPublicKey(byte[] bytes, byte[] key) {
        return doFinal(Objects.requireNonNull(getCipher(key, PUBLIC_KEY, DECRYPT_MODE)), bytes);

    }

    /**
     * @return keyPair or null if something wrong
     */
    @Nullable
    public static KeyPair genRSA() {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generator == null ? null : generator.generateKeyPair();
    }
}
