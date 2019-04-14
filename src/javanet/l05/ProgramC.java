package javanet.l05;

import java.io.IOException;
import java.net.*;

public class ProgramC extends Thread {
    private Bus bus = new Bus();
    private DatagramSocket socket;

    ProgramC(String host, int port, String bus_id, int maxPos) {
        try {
            bus.host = InetAddress.getByName(host);
            bus.port = port;
            bus.id = bus_id;
            bus.maxPos = maxPos;
            bus.pos = 0;
            socket = new DatagramSocket(0);
            start();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                bus.moveOn();
                if (Math.random() < 0.00001f) bus.pos = -1;
                byte[] bytes = Packets.Builder.positionReport(bus);
                socket.send(new DatagramPacket(bytes, bytes.length, bus.host, bus.port));
                if (bus.pos == -1) break;
                System.out.println(Thread.currentThread().getName() + " pos send!");
                Thread.sleep((long) (Math.random() * 2000 + 1000));
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " Exit!");
    }
}
