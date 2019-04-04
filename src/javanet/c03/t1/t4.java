/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//4.	编写一个Java程序，该程序能够通过TCP协议与daytime服务通信，获取并打印服务器时间（输出时间格式为“2019年3月10日  18时30分”）。
public class t4 {
    public static void main(String[] args) throws IOException, ParseException {
        SimpleDateFormat in = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd");
        SimpleDateFormat out = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        System.out.println(out.format(in.parse(new BufferedReader(new InputStreamReader(new Socket(InetAddress.getLocalHost(), 13).getInputStream())).readLine())));
    }
}
