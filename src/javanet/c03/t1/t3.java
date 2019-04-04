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
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

//3.	编写一个简单的Java网络程序，该程序能够通过扫描服务对应的相关端口判断服务器上的daytime、echo以及web服务是否处于运行状态。
public class t3 {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String daytime;
        try {
            daytime = new BufferedReader(new InputStreamReader(new Socket(address, 13).getInputStream())).readLine();
            if (daytime != null && daytime.length() != 0) System.out.println("daytime:" + daytime);
            else System.out.println("daytime service unavailable");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("daytime service unavailable");
        }

        try {
            Socket echo = new Socket(address, 7);
            byte[] echoTest = new byte[16];
            echo.getOutputStream().write(echoTest);
            int x = echo.getInputStream().read(echoTest);
            if (x == 16) System.out.println("Echo service test successful");
            else System.out.println("Echo service unavailable");
            echo.close();
        } catch (IOException e) {
            System.out.println("Echo service unavailable");
            e.printStackTrace();
        }

        try {
            Socket web = new Socket(address, 80);
            web.setKeepAlive(true);
            web.getOutputStream().write(("GET / HTTP/1.1\" 200 2826 \"-\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36").getBytes(StandardCharsets.UTF_8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(web.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
            System.out.println("web service test successful");
            web.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("web service unavailable");
        }
    }
}
