/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t1;

import java.net.InetAddress;
import java.net.UnknownHostException;

//1.	编写一个简单的Java网络程序，该程序能够获取常见网站的IP地址。
public class t1 {
    public static void main(String[] args) throws UnknownHostException {
        System.out.println(InetAddress.getByName("www.baidu.com"));
    }
}
