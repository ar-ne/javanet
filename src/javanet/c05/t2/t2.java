package javanet.c05.t2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

//2.	编写一Java网络程序，该程序能够响应浏览器发过来的GET请求，完成2个数的相乘，并向浏览器返回以HTML格式呈现的结果。
public class t2 {
    static String reqPrefix = "http://127.0.0.1:0000";
    static String header = "HTTP/1.1 200\n" +
            "Content-Type: text/html;charset=UTF-8\n\n";
    private static String pathPrefix = "src/javanet/c05/t1/t1";
    private static String imgHeader = "HTTP/1.1 200\n" +
            "Accept-Ranges: bytes\n" +
            "Content-Type: image/jpeg\n\n";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        System.out.println("http://127.0.0.1:" + serverSocket.getLocalPort());
        while (!serverSocket.isClosed()) {
            Socket s = serverSocket.accept();
            new Thread(() -> reply(s)).start();
        }
    }

    private static void reply(Socket socket) {
        try {
            String line;
            String GET = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("GET ")) {
                    GET = line.substring(3).trim();
                    break;
                }
            }
            GET = GET.substring(0, GET.lastIndexOf(" ")).trim();
            URL url = new URL(reqPrefix + GET);
            System.out.println(url);
            String query = url.getQuery();
            double x = Double.parseDouble(query.substring(0, query.indexOf("&")).trim());
            double y = Double.parseDouble(query.substring(query.indexOf("&") + 1).trim());
            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(("ans:" + (x * y)).getBytes());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
