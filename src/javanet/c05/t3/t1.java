package javanet.c05.t3;

import java.io.IOException;
import java.net.URL;

//1.	分析给定的网页，编写一个简单的网络爬虫程序，其要求如下：
//（1）	能够按照给定网页中的登录表单实现自动登录并能获取登录后的页面内容；
//（2）	能够获取给定网页中所包含的css、js、图片以及超链接的对应地址。
public class t1 {
    static String target = "http://localhost:8080/jn_16201235_Web_exploded/?a=123&b=456";

    public static void main(String[] args) throws IOException {
        URL url = new URL(target);
        int a = url.openStream().available();
        byte[] bytes = new byte[a];
        int ra = url.openStream().read(bytes);
        String reply = new String(bytes);
        System.out.println(reply);
        if (reply.contains("success")) System.out.println("登陆成功");
        else System.out.println("登陆失败");

    }
}
