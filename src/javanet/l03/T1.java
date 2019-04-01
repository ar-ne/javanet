package javanet.l03;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

//1．	利用TCP协议实现一个可以从服务器读取时间的网络程序。
public class T1 {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 2007);
        DataInputStream stream = new DataInputStream(socket.getInputStream());
        System.out.print(stream.readInt() + "年");
        System.out.print(((stream.readByte() & 0xff) + 1) + "月");
        System.out.print((stream.readByte() & 0xff) + "日");
        System.out.print((stream.readByte() & 0xff) + "时");
        System.out.print((stream.readByte() & 0xff) + "分");
        System.out.print((stream.readByte() & 0xff) + "秒");
        stream.close();
        socket.close();
    }
}
