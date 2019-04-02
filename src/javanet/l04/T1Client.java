package javanet.l04;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Calendar;

public class T1Client extends Thread {
    private boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            {
                new T1Client().start();
                Thread.sleep((long) (Math.random() * 5000));
            }
        }

    }

    private static int randPort() {
        return (int) (Math.random() * 55290 + 10240);
    }

    @Override
    public void run() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            int port = 65534;
            int localPort = randPort();
            DatagramSocket socket = null;
            while (socket == null) {
                try {
                    socket = new DatagramSocket(localPort);
                } catch (SocketException e) {
                    System.out.println("Can not bind port : " + localPort);
                    localPort = randPort();
                }
            }
            System.out.println("localPort : " + localPort);
            int counter = 0;
            while (running) {
                byte[] bytes = ByteBuffer.allocate(16).putLong(Calendar.getInstance().getTime().getTime()).putDouble(Math.random() * 200 - 100).array();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);
                socket.send(packet);
                System.out.println(localPort + " => Packet send! " + counter++);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
