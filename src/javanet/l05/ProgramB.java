package javanet.l05;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgramB extends Thread {
    ConcurrentHashMap<String, Bus> busStopMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Bus> bus = new ConcurrentHashMap<>();
    private boolean TCPServerRunning = false, UDPServerRunning = false;
    private ServerSocket TCPsocket;
    private DatagramSocket UDPsocket;
    private ExecutorService service = Executors.newFixedThreadPool(16);

    public ProgramB() {
        loadBusInfo("BusStopInfo.txt");
        try {
            TCPsocket = new ServerSocket(0);
            UDPsocket = new DatagramSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!TCPsocket.isClosed()) System.out.println("TCP Server running on " + TCPsocket.getLocalPort());
        else System.out.println("TCP Server closed!");
        if (!UDPsocket.isClosed()) System.out.println("UDP Server running on " + UDPsocket.getLocalPort());
        else System.out.println("UDP Server closed!");
        if (!TCPsocket.isClosed() && !UDPsocket.isClosed()) start();
    }

    public void loadBusInfo(String fn) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fn)));
            String line;
            LinkedList<String> list = null;
            while (true) {
                line = reader.readLine();
                if (line != null && line.isEmpty() && list == null) list = new LinkedList<>();
                else if ((line == null || line.isEmpty()) && list != null) {
                    Bus bus = new Bus();
                    bus.setId(list.getFirst());
                    list.removeFirst();
                    bus.setStops(list);
                    busStopMap.put(bus.id, bus);
                    System.out.println(bus);
                    list = null;
                    if (line == null) break;
                } else if (list != null) {
                    list.add(line.trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdownNow() {
        TCPServerRunning = false;
        UDPServerRunning = false;
        try {
            System.out.println("Force shutdown!");
            TCPsocket.close();
            UDPsocket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        new Thread(this::TCPServerThread, "TCP Server Thread").start();
        new Thread(this::UDPServerThread, "UDP Server Thread").start();
    }

    private void UDPServerThread() {
        UDPServerRunning = true;
        while (!UDPsocket.isClosed() && UDPServerRunning) {
            try {
                byte[] bytes = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                UDPsocket.receive(packet);
            } catch (IOException e) {
                if (UDPServerRunning) e.printStackTrace();
            }
        }
    }

    private void UDPServerSubThread() {
    }

    private void TCPServerThread() {
        TCPServerRunning = true;
        while (!TCPsocket.isClosed() && TCPServerRunning) {
            try {
                Socket s = TCPsocket.accept();
                service.submit(() -> TCPServerSubThread(s));
            } catch (IOException e) {
                if (TCPServerRunning) e.printStackTrace();
            }
        }
        TCPServerRunning = false;
    }

    private void TCPServerSubThread(Socket socket) {
        try {
            String currentBus_id;
            while (!socket.isClosed()) {
                byte[] bytes = new byte[10240];
                int length = socket.getInputStream().read(bytes);
                String data = Packets.getString(bytes);
                if (data.startsWith("1")) {
                    String bus_id = Packets.Reader.clientQuery(data);
                    currentBus_id = bus_id;
                    socket.getOutputStream().write(Packets.Builder.busStopList(busStopMap.get(bus_id)));
                    socket.getOutputStream().flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        if (TCPServerRunning) return TCPsocket.getLocalPort();
        else return -1;
    }
}
