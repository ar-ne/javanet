package javanet.c04.t2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

//2.	编写一Java网络程序，其工作过程如下：
//客户端能够随机生成若干个数（数值范围0-1000，数值个数不定）并通过UDP协议发送到服务器；
// 服务器端能够接收到这些数值后将其累加并将结果返回给客户端；客户端接收到结果后打印后结束。
public class t2 {
    public static void main(String[] args) {
        AtomicInteger port = new AtomicInteger(0);
        Runnable client = () -> {
            int x = (int) (Math.random() * 6 + 1);
            ByteBuffer buffer = ByteBuffer.allocate(x * 4);
            StringBuilder builder = new StringBuilder().append(Thread.currentThread().getName()).append("=>");
            int cr = 0;
            for (int i = 0; i < x; i++) {
                int a = (int) (Math.random() * 1000);
                buffer.putInt(a);
                builder.append(a).append(" ");
                cr += a;
            }
            try {
                DatagramSocket socket = new DatagramSocket(0);
                while (port.get() == 0) Thread.sleep(10);
                DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.array().length, InetAddress.getLocalHost(), port.get());
                socket.send(packet);
                socket.receive(packet);
                builder.append(" Server Result : ").append(ByteBuffer.wrap(packet.getData()).getInt()).append(" Client Result : ").append(cr);
                System.out.println(builder);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        };
        Runnable server = () -> {
            try {
                DatagramSocket socket = new DatagramSocket(0);
                System.out.println("Server UP,random port : " + socket.getLocalPort());
                port.set(socket.getLocalPort());
                while (true) {
                    byte[] bytes = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    socket.receive(packet);
                    new Thread(() -> ST(socket, packet.getData(), packet.getAddress(), packet.getPort())).start();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(server).start();
        new Thread(client).start();
    }

    private static void ST(DatagramSocket socket, byte[] data, InetAddress host, int port) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int sr = 0;
        for (int i = 0; i < buffer.array().length / 4; i++) sr += buffer.getInt();
        try {
            socket.send(new DatagramPacket(ByteBuffer.allocate(4).putInt(sr).array(), 4, host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
