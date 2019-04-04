package javanet.c04.t1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

//2.	编写一个Java程序，该程序能够通过UDP协议与ntp服务进行通信，获取并打印服务器时间（输出时间格式为“2019年3月10日  18时30分20秒”）。
//说明：服务器可以选择“ntp1.aliyun.com”，关于ntp服务的详情请参考“RFC 4330”与“RFC 5909”以及网络上的相关资料。
public class t2 {
    public static void main(String[] args) throws IOException {
        InetAddress host = InetAddress.getByName("ntp1.aliyun.com");
        DatagramSocket socket = new DatagramSocket(0);
        int port = 123;
        byte[] bytes = new byte[48];
        bytes[0] = 0x1b;
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
        socket.send(packet);
        socket.receive(packet);
        long ss = 0;
        for (int i = 40; i < 44; i++) {
            ss = (ss << 8) | (bytes[i] & 0xff);
        }
        System.out.println(packet.getData().length);
        System.out.println(new SimpleDateFormat("yyyy年M月d日  HH时mm分ss秒").format(new Date((ss - 2208988800L) * 1000)));

    }
}
/**
 * https://tools.ietf.org/html/rfc5905
 * https://tools.ietf.org/html/rfc5906
 * https://lettier.github.io/posts/2016-04-26-lets-make-a-ntp-client-in-c.html
 * <p>
 * typedef struct
 * {
 * <p>
 * uint8_t li_vn_mode;      // Eight bits. li, vn, and mode.
 * // li.   Two bits.   Leap indicator.
 * // vn.   Three bits. Version number of the protocol.
 * // mode. Three bits. Client will pick mode 3 for client.
 * <p>
 * uint8_t stratum;         // Eight bits. Stratum level of the local clock.
 * uint8_t poll;            // Eight bits. Maximum interval between successive messages.
 * uint8_t precision;       // Eight bits. Precision of the local clock.
 * <p>
 * uint32_t rootDelay;      // 32 bits. Total round trip delay time.
 * uint32_t rootDispersion; // 32 bits. Max error aloud from primary clock source.
 * uint32_t refId;          // 32 bits. Reference clock identifier.
 * <p>
 * uint32_t refTm_s;        // 32 bits. Reference time-stamp seconds.
 * uint32_t refTm_f;        // 32 bits. Reference time-stamp fraction of a second.
 * <p>
 * uint32_t origTm_s;       // 32 bits. Originate time-stamp seconds.
 * uint32_t origTm_f;       // 32 bits. Originate time-stamp fraction of a second.
 * <p>
 * uint32_t rxTm_s;         // 32 bits. Received time-stamp seconds.
 * uint32_t rxTm_f;         // 32 bits. Received time-stamp fraction of a second.
 * <p>
 * uint32_t txTm_s;         // 32 bits and the most important field the client cares about. Transmit time-stamp seconds.
 * uint32_t txTm_f;         // 32 bits. Transmit time-stamp fraction of a second.
 * <p>
 * } ntp_packet;              // Total: 384 bits or 48 bytes.
 */
