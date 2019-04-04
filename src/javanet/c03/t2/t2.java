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
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

//2.	编写一Java网络程序，其工作过程如下：
//客户端能够接收用户输入的两个数并发送到服务器；服务器端能够接收到这两个数并将其相加并将结果返回给客户端；客户端接收到结果后打印出来并关闭连接。
public class t2 {
    public static void main(String[] args) {
        AtomicInteger port = new AtomicInteger(0);
        Runnable server = () -> {
            try {
                String prefix = "[Server]";
                ServerSocket serverSocket = new ServerSocket(port.get());
                port.set(serverSocket.getLocalPort());
                System.out.println(prefix + "Listening on port:" + port.get());
                Socket socket = serverSocket.accept();
                System.out.println(prefix + "Accept income connection:" + socket);
                ByteBuffer buffer = ByteBuffer.allocate(8);
                int a = 0;
                int r = socket.getInputStream().read(buffer.array());
                for (int i = 0; i < r / 4; i++)
                    a += buffer.getInt();
                System.out.println(prefix + "received " + r);
                System.out.println(prefix + "result " + a);
                socket.getOutputStream().write(ByteBuffer.allocate(8).putInt(a).array());
                System.out.println(prefix + "Data wrote");
                socket.close();
                serverSocket.close();
                System.out.println(prefix + "server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Runnable client = () -> {
            try {
                String prefix = "[Client]";
                ByteBuffer buffer = ByteBuffer.allocate(8);
                Scanner scanner = new Scanner(System.in);
                System.out.print(prefix + "input 2 int:");
                for (int i = 0; i < 2; i++) {
                    buffer.putInt(scanner.nextInt());
                }
                while (port.get() == 0) ;
                Socket socket = new Socket(InetAddress.getLocalHost(), port.get());
                socket.getOutputStream().write(buffer.array());
                socket.getInputStream().read(buffer.array());
                System.out.println(prefix + "result:" + buffer.getInt(0));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(server).start();
        new Thread(client).start();
    }
}
