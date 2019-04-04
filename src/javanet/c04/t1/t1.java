package javanet.c04.t1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

//1.	编写一个Java程序，该程序能够通过UDP与time服务通信，获取并打印服务器时间（输出时间格式为“2019年3月10日  18时30分20秒”）。
//说明：服务器可以选择“time.nist.gov”，注意time协议与daytime协议不同，该协议详情请参考“RFC 868”以及网络上的相关资料。
public class t1 {
    public static void main(String[] args) throws IOException {
        InetAddress host = InetAddress.getByName("time.nist.gov");
        int port = 37;
        DatagramSocket socket = new DatagramSocket(0);
        byte[] bytes = new byte[4];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
        socket.receive(packet);
        long ss = 0;
        for (int i = 0; i < 4; i++) {
            ss = (ss << 8) | (bytes[i] & 0xff);
        }
        System.out.println(new SimpleDateFormat("yyyy年M月d日  HH时mm分ss秒").format(new Date((ss - 2208988800L) * 1000)));
    }
}
