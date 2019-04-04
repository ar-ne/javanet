/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t1;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

//2.	编写一个简单的Java网络程序，该程序能够获取本机的IP地址与MAC地址。
public class t2 {
    public static void main(String[] args) throws UnknownHostException, SocketException {
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        byte[] x = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < x.length; i++) {
            if (i != 0) stringBuffer.append("-");
            stringBuffer.append(Integer.toHexString(x[i] & 0xff).length() == 1 ? 0 + Integer.toHexString(x[i] & 0xff) : Integer.toHexString(x[i] & 0xff));
        }
        System.out.println(stringBuffer.toString().toUpperCase());
    }
}
