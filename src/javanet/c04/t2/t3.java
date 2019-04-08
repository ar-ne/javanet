package javanet.c04.t2;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

//3.	编写一Java网络程序，模拟一个车辆定位程序，其工作过程如下：
//车辆（客户端）能够定时向服务器发送自己的坐标位置，
// 如果车辆离中心位置超出限定距离，则会收到服务器的持续报警信息直至距离小于限定距离。
public class t3 {
    static DatagramSocket socket;
    static double limit = 1f;

    public static void main(String[] args) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(0);
        Runnable server = () -> {
            try {
                while (true) {
                    byte[] bytes = new byte[16];
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    socket.receive(packet);
                    new Thread(() -> response(packet.getData(), packet.getAddress(), packet.getPort())).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(server).start();
        new Thread(new Client(InetAddress.getLocalHost(), socket.getLocalPort())).start();
    }

    private static void response(byte[] bytes, InetAddress host, int port) {
        ByteBuffer request = ByteBuffer.wrap(bytes);
        double x = request.getDouble(), y = request.getDouble();
        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        if (distance > limit) {
            try {
                System.out.println(port + " out of range! " + distance);
                ByteBuffer response = ByteBuffer.allocate(8).putDouble(distance - limit);
                socket.send(new DatagramPacket(response.array(), response.array().length, host, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println(port + " update distance : " + distance);
    }

    static class Client implements Runnable {
        DatagramSocket socket;
        int serverPort;
        InetAddress host;
        double x, y;
        byte[] bytes = new byte[8];

        public Client(InetAddress host, int serverPort) {
            this.host = host;
            this.serverPort = serverPort;
            try {
                socket = new DatagramSocket(0);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            new Thread(this::receiveResponseThread).start();
            while (true) {
                try {
                    sendPos();
                    move();
                    Thread.sleep(1000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void receiveResponseThread() {
            try {
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                while (true) {
                    socket.receive(packet);
                    System.out.println(Thread.currentThread().getName() + "->Alarm! Out of range : " + ByteBuffer.wrap(packet.getData()).getDouble());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void move() {
            x += Math.random() > 0.5f ? Math.random() : -Math.random();
            y += Math.random() > 0.5f ? Math.random() : -Math.random();
        }

        private void sendPos() throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(16).putDouble(x).putDouble(y);
            socket.send(new DatagramPacket(buffer.array(), buffer.array().length, host, serverPort));
        }
    }
}
