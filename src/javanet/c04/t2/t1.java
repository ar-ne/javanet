package javanet.c04.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

//1.	编写一个Java服务器程序，该程序能够返回一个随机的校验码
// （端口号，校验码长度自定，由数字与字母组成，每个字符占1个字节，区分大小写）给客户端，
// 请使用sockit工具与其通信并测试程序运行是否正常。
public class t1 {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(0);
        DatagramPacket packet = new DatagramPacket(new byte[4], 4);
        System.out.println("Local port : " + socket.getLocalPort());
        while (true) {
            socket.receive(packet);
            new Thread(() -> reply(socket, packet)).start();
        }
    }

    private static void reply(DatagramSocket socket, DatagramPacket packet) {
        StringBuilder stringBuilder = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            if (Math.random() > (2.0f / 3.0f)) stringBuilder.append((int) (Math.random() * 10));
            else {
                if (Math.random() > 0.5f) stringBuilder.append((char) ('a' + (int) (Math.random() * 26)));
                else stringBuilder.append((char) ('A' + (int) (Math.random() * 26)));
            }
        }
        try {
            socket.send(new DatagramPacket(stringBuilder.toString().getBytes(), stringBuilder.toString().getBytes().length, packet.getAddress(), packet.getPort()));
            System.out.println(stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
