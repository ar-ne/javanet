package javanet.e06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8888);

            while (true) {
                Socket socket = ss.accept();
                BufferedReader bd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String header;
                int length = 0;
                String sbx = "";
                while ((header = bd.readLine()) != null && !header.isEmpty()) {
                    System.out.println(header);
                    if (header.startsWith("GET")) {
                        String condition = header.substring(header.indexOf("/?") + 2, header.indexOf("HTTP/"));
                        System.out.println("GET参数是：" + condition);
                        sbx = condition;
                    }
                }

                if (sbx.length() > 0) {
                    try {
                        int a = Integer.parseInt(sbx.substring(0, sbx.indexOf("&")).trim());
                        int b = Integer.parseInt(sbx.substring(sbx.indexOf("&") + 1).trim());
                        sbx = String.valueOf(a * b);
                    } catch (Exception ignored) {
                    }
                }
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.println("HTTP/1.1 200 OK");
                pw.println("Content-type:text/html");
                pw.println();
                pw.print(sbx);
                pw.println();
                pw.flush();
                pw.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}