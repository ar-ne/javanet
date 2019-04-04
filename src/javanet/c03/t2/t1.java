/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

//1.	编写一个Java服务器程序，该程序能够返回一个随机数给客户端，请使用telnet程序与其通信并测试程序运行是否正常。
public class t1 {
    public static void main(String[] args) throws IOException, InterruptedException {
        AtomicInteger port = new AtomicInteger(0);
        Runnable server = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port.get());
                port.set(serverSocket.getLocalPort());
                System.out.println("Listening on port:" + port.get());
                Socket socket = serverSocket.accept();
                System.out.println("Accept income connection:" + socket);
                socket.getOutputStream().write(String.valueOf(Math.random()).getBytes());
                System.out.println("Data wrote");
                socket.close();
                serverSocket.close();
                System.out.println("server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(server).start();
        while (port.get() == 0)
            Thread.sleep(10);
    }
}
