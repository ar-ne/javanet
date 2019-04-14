package javanet.c05.t2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//1.	编写一个简单的HTTP服务器，该程序能够根据浏览器发送过来的地址信息返回对应的静态页面（含图片）。
public class t1 {
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            String GET = "";
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                if (line.startsWith("GET ")) {
                    GET = line.substring(3).trim();
                    break;
                }
            }
            GET = GET.substring(0, GET.lastIndexOf(" ")).trim();
            System.out.println("GET:" + GET);
            String fn = GET.equals("/") ? "/index.jsp" : GET;
            FileInputStream fileInputStream = new FileInputStream(new File(pathPrefix + fn));
            int availible = fileInputStream.available();
            System.out.println(availible);
            byte[] bytes = new byte[availible];
            int readed = fileInputStream.read(bytes);
            fileInputStream.close();
            if (GET.endsWith(".jpg"))
                socket.getOutputStream().write(imgHeader.getBytes());
            else
                socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(bytes);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
