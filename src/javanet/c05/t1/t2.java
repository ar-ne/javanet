package javanet.c05.t1;

import java.net.MalformedURLException;
import java.net.URL;

//2.	编写一个Java程序，该程序能够从一个给定的url中获取协议、主机、端口号、路径、请求参数、定位位置等信息。
public class t2 {
    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://127.0.0.1:8080/index.html?x=1&y=2");
        System.out.println(url);
        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());
        System.out.println(url.getPath());
        System.out.println(url.getQuery());
    }
}
