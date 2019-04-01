package javanet.l3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class DaytimeServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(2007);
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> writeTime(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTime(Socket s) {
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            Calendar current = Calendar.getInstance();
            out.writeInt(current.get(Calendar.YEAR));
            out.writeByte(current.get(Calendar.MONTH));
            out.writeByte(current.get(Calendar.DAY_OF_MONTH));
            out.writeByte(current.get(Calendar.HOUR_OF_DAY));
            out.writeByte(current.get(Calendar.MINUTE));
            out.writeByte(current.get(Calendar.SECOND));
            out.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
