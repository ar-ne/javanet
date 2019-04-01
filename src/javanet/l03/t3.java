/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.l03;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class t3 {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(InetAddress.getLocalHost(), 7);
//        socket.setKeepAlive(true);
        InputStreamReader reader = new InputStreamReader(socket.getInputStream());
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        Runnable w = () -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String line = scanner.next();
                    writer.write(line);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable r = () -> {
            try {
                while (true) {
                    char[] chars = new char[1024000];
                    int x = reader.read(chars);
//                    System.out.print("read " + x + " chars=>");
                    System.out.println(chars);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(w).start();
        new Thread(r).start();
    }
}
