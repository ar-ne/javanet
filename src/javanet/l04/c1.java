/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.l04;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class c1 {
    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName("time.nist.gov");
        DatagramSocket socket = new DatagramSocket(5848);
        byte[] buf = new byte[4];
        DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, 37);
        DatagramPacket packet2 = new DatagramPacket(buf, buf.length);
        socket.send(packet);
        socket.receive(packet2);
//        System.out.println(packet2);

        long ss = 0;
        for (int i = 0; i < 4; i++) {
            ss = (ss << 8) | (buf[i] & 0xff);
        }
        long ms = (ss - 2208988800L) * 1000;
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        System.out.println(format.format(date));
    }
}
