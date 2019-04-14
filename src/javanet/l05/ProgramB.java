package javanet.l05;

import com.rits.cloning.Cloner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javanet.l05.Packets.getString;

public class ProgramB extends Thread {
    ConcurrentHashMap<String, Bus> busStopMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, HashSet<Integer>> busPosMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Bus> UDPBusMap = new ConcurrentHashMap<>();
    private boolean TCPServerRunning = false, UDPServerRunning = false;
    private ServerSocket TCPsocket;
    private DatagramSocket UDPsocket;
    private ExecutorService TCPservice = Executors.newFixedThreadPool(16);
    private ExecutorService UDPservice = Executors.newFixedThreadPool(32);

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
        new Thread(this::positionUpdateThread).start();
        while (!UDPsocket.isClosed() && UDPServerRunning) {
            try {
                byte[] bytes = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                UDPsocket.receive(packet);
                UDPservice.submit(() -> UDPServerSubThread((new Cloner()).deepClone(packet)));
            } catch (IOException e) {
                if (UDPServerRunning) e.printStackTrace();
            }
        }
    }

    private void UDPServerSubThread(DatagramPacket packetClone) {
        Bus received = Packets.Reader.parsePositionReport(getString(packetClone.getData()));
        String key = packetClone.getAddress().toString() + packetClone.getPort();
        Bus bus;
        if (!UDPBusMap.containsKey(key)) {
            bus = new Bus();
            UDPBusMap.put(key, bus);
            bus.host = packetClone.getAddress();
            bus.port = packetClone.getPort();
        }
        if (received.pos == -1) {
            UDPBusMap.remove(key);
            return;
        }
        bus = UDPBusMap.get(key);
        bus.pos = received.pos;
        bus.id = received.id;
        bus.posLastUpdate = System.currentTimeMillis();
    }

    private void positionUpdateThread() {
        while (true) {
            try {
                busPosMap.clear();
                for (Map.Entry<String, Bus> entry : UDPBusMap.entrySet()) {
                    if (!busPosMap.containsKey(entry.getValue().id))
                        busPosMap.put(entry.getValue().id, new HashSet<>());
                    busPosMap.get(entry.getValue().id).add(entry.getValue().pos);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void TCPServerThread() {
        TCPServerRunning = true;
        while (!TCPsocket.isClosed() && TCPServerRunning) {
            try {
                Socket s = TCPsocket.accept();
                TCPservice.submit(() -> TCPServerSubThread(s));
            } catch (IOException e) {
                if (TCPServerRunning) e.printStackTrace();
            }
        }
        TCPServerRunning = false;
    }


    private void TCPServerSubThread(Socket socket) {
        System.out.println(socket);
        try {
            while (!socket.isClosed()) {
                byte[] bytes = new byte[10240];
                int length = socket.getInputStream().read(bytes);
                String data = getString(bytes);
                String bus_id = Packets.Reader.clientQuery(data);
                byte[] x;
                switch (data.charAt(0)) {
                    case '1':
                        if (busStopMap.containsKey(bus_id))
                            x = Packets.Builder.busStopList(busStopMap.get(bus_id));
                        else x = Packets.Builder.busStopList(new Bus());
                        socket.getOutputStream().write(x);
                        socket.getOutputStream().flush();
                        break;
                    case '2':
                        if (busPosMap.containsKey(bus_id))
                            x = Packets.Builder.busPositionList(busPosMap.get(bus_id));
                        else x = Packets.Builder.busPositionList(new HashSet<>());
                        socket.getOutputStream().write(x);
                        socket.getOutputStream().flush();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTCPport() {
        if (TCPServerRunning) return TCPsocket.getLocalPort();
        else return -1;
    }

    public int getUDPport() {
        if (UDPServerRunning) return UDPsocket.getLocalPort();
        else return -1;
    }
}
