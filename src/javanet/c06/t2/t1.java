package javanet.c06.t2;

import javanet.c06.t1.t3;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import static javanet.c06.t1.t1.readAll;
import static javanet.c06.t1.t2.genAES;
import static javanet.c06.t1.t3.*;

//1.	设有2台主机A、B，请模拟以下过程：
//（1）	A主机上有公钥文件， B主机上有私钥文件；
//（2）	A主机连接B主机时首先将生成一个随机密码，并将密码通过公钥加密发送给B主机；
//（3）	B主机接收到A主机的密码之后将首先计算文件的消息摘要，然后利用接收到的密码采用对称加密算法将文件与消息摘要一并传送给A主机；
//（4）	A主机接收到文件后将文件解密并通过消息摘要验证文件是否完整无误。
public class t1 {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static void main(String[] args) throws InterruptedException {
        KeyPair keyPair = t3.genRSA();
        if (keyPair == null) return;
        Host hostA = new HostA(encodeKey(keyPair.getPublic()));
        Host hostB = new HostB(encodeKey(keyPair.getPrivate()));
        hostA.setTarget(hostB);
        hostB.setTarget(hostA);
        hostA.start();
        hostB.start();
        hostA.join();
        hostB.join();
    }

    @Nullable
    private static byte[] getDigest(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkDigest(byte[] d1, byte[] d2) {
        return Arrays.equals(d1, d2);
    }

    private static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    static abstract class Host extends Thread {
        byte[] RSAKey, AESKey;
        int port;
        InetAddress host;
        DatagramSocket socket;

        Host(byte[] RSAKey) {
            this.RSAKey = RSAKey;
            try {
                socket = new DatagramSocket(0);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        void setTarget(Host host) {
            this.host = host.socket.getInetAddress();
            if (this.host == null) {
                try {
                    this.host = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            this.port = host.socket.getLocalPort();
        }

        void sendData(byte[] data) {
            try {
                byte[] d = encrypt(data);
                socket.send(new DatagramPacket(d, d.length, host, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Nullable
        byte[] receiveData() {
            try {
                byte[] buffer = new byte[10240];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                return decrypt(Arrays.copyOf(packet.getData(), packet.getLength()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        abstract byte[] encrypt(byte[] data);

        abstract byte[] decrypt(byte[] data);
    }

    static class HostA extends Host {
        HostA(byte[] key) {
            super(key);
        }

        @Override
        public void run() {
            AESKey = Objects.requireNonNull(genAES()).getEncoded();
            System.out.println("HostA => Send AES key : " + toHexString(AESKey));
            sendData(AESKey);
            byte[] digest = receiveData();
            byte[] data = receiveData();
            @Nullable byte[] localDigest = getDigest(data);
            System.out.println("HostA => Received digest : " + toHexString(Objects.requireNonNull(digest)));
            System.out.println("HostA => Local digest    : " + toHexString(Objects.requireNonNull(localDigest)));
            System.out.println("HostA => Digest check success = " + checkDigest(digest, localDigest));
        }

        @Override
        byte[] encrypt(byte[] data) {
            return encryptByPublicKey(data, RSAKey);
        }

        @Override
        byte[] decrypt(byte[] data) {
            return decryptByPublicKey(data, RSAKey);
        }
    }

    static class HostB extends Host {
        HostB(byte[] key) {
            super(key);
        }

        @Override
        public void run() {
            AESKey = receiveData();
            System.out.println("HostB => Receive AES key : " + toHexString(Objects.requireNonNull(AESKey)));
            byte[] data = readAll("TestData/test.txt");
            byte[] digest = getDigest(data);
            System.out.println("HostB => Sending digest : " + toHexString(Objects.requireNonNull(digest)));
            sendData(digest);
            sendData(data);
            System.out.println("HostB => Data sent,DONE!");
        }

        @Override
        byte[] encrypt(byte[] data) {
            return encryptByPrivateKey(data, RSAKey);
        }

        @Override
        byte[] decrypt(byte[] data) {
            return decryptByPrivateKey(data, RSAKey);
        }
    }
}
