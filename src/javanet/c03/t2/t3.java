/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//3.	编写一Java网络程序，其工作过程如下：
//客户端能够接收用户输入的任意个数并发送到服务器；服务器端能够接收到这若干个数之后计算其和、
// 平均数以及方差并返回给客户端；客户端接收到结果后打印出来并继续接收用户的下一轮数据；服务器端在接收到客户端的3轮数据之后关闭连接。
public class t3 {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger port = new AtomicInteger(0);

        Runnable server = () -> {
            try {
                String prefix = "[Server]";
                System.out.println(prefix + ByteBuffer.allocate(1).order());
                ServerSocket serverSocket = new ServerSocket(port.get());
                port.set(serverSocket.getLocalPort());
                System.out.println(prefix + "Listening on port:" + port.get());
                ArrayList<Socket> sockets = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Socket socket = serverSocket.accept();
                    sockets.add(socket);
                    System.out.println(prefix + "Accept income connection:" + socket);
                    new Thread(() -> serverSocketThread(socket)).start();
                }
                while (true) {
                    int count = 0;
                    for (Socket socket : sockets)
                        if (socket.isClosed()) count++;
                    if (count == sockets.size()) break;
                    Thread.sleep(20);
                }
                serverSocket.close();
                System.out.println(prefix + "server closed");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        Runnable client = () -> {
            try {
                String prefix = "[Client]";
                System.out.println(prefix + ByteBuffer.allocate(1).order());
                Scanner scanner = new Scanner(System.in);
                System.out.print(prefix + "input total amount:");
                int t = scanner.nextInt();
                System.out.print(prefix + "input :");
                ByteBuffer buffer = ByteBuffer.allocate(8 * t);
                for (int i = 0; i < t; i++)
                    buffer.putDouble(scanner.nextDouble());
                while (port.get() == 0) ;
                Socket socket = new Socket(InetAddress.getLocalHost(), port.get());
                System.out.println(prefix + "send total amount:" + t);
                socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(t).array());
                System.out.println(prefix + "send data");
                socket.getOutputStream().write(buffer.array());
                System.out.println(prefix + "receive data");
                buffer = ByteBuffer.allocate(3 * 8);
                socket.getInputStream().read(buffer.array());
                System.out.println(prefix + "result:");
                for (int i = 0; i < 3; i++) {
                    System.out.print(buffer.getDouble() + " ");
                }
                System.out.println();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(server).start();
        Thread c;
        for (int i = 0; i < 3; i++) {
            c = new Thread(client);
            c.start();
            c.join();
        }
    }

    public static byte[] results(List<Double> arr) {
        ByteBuffer buffer = ByteBuffer.allocate(3 * 8);
        double sum = 0, avg, variance = 0;
        for (Double aDouble1 : arr) sum += aDouble1;
        avg = sum / (double) arr.size();
        for (Double aDouble : arr)
            variance += Math.pow(avg - aDouble, 2);
        variance = variance / (double) arr.size();
        buffer.putDouble(sum);
        buffer.putDouble(avg);
        buffer.putDouble(variance);
        return buffer.array();
    }

    private static void serverSocketThread(Socket socket) {
        try {
            String prefix = "[ServerSocketThread=>" + Thread.currentThread().getName() + "]";
            ByteBuffer buffer = ByteBuffer.allocate(4);
            int a;
            int r = socket.getInputStream().read(buffer.array());
            a = buffer.getInt();
            System.out.println(prefix + "read " + r + " bytes");
            System.out.println(prefix + "count = " + a);
            buffer = ByteBuffer.allocate(8 * a);
            r = socket.getInputStream().read(buffer.array());
            System.out.println(prefix + "read " + r + " bytes,r/8=" + r / 8.0f);
            if (r / 8 == a) {
                ArrayList<Double> arrayList = new ArrayList<>();
                for (int i = 0; i < a; i++) {
                    arrayList.add(buffer.getDouble());
                }
                socket.getOutputStream().write(results(arrayList));
            }
            System.out.println(prefix + "Data wrote");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
