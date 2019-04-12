package javanet.l05;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgramB extends Thread {
    ConcurrentHashMap<String, Bus> busMap = new ConcurrentHashMap<>();
    private boolean running = false;
    private ServerSocket socket;
    private ExecutorService service = Executors.newFixedThreadPool(16);

    public ProgramB() {
        loadBusInfo("BusStopInfo.txt");
    }

    public void loadBusInfo(String fn) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fn)));
            String line;
            LinkedList<String> list = null;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() && list == null) list = new LinkedList<>();
                else if (line.isEmpty()) {
                    Bus bus = new Bus();
                    bus.setId(list.getFirst());
                    list.removeFirst();
                    bus.setStops(list);
                    busMap.put(bus.id, bus);
                    System.out.println(bus);
                    list = null;
                } else if (list != null) {
                    list.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(0);
            running = true;
            while (true) {
                Socket s = socket.accept();
                service.submit(() -> serverSubThread(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serverSubThread(Socket socket) {
        try {
            while (!socket.isClosed()) {
                byte[] bytes = new byte[10240];
                int length = socket.getInputStream().read(bytes);
                String data = Packets.getString(bytes);
                if (data.startsWith("1")) {
                    String bus_id = Packets.Reader.clientQuery(data);
                    socket.getOutputStream().write(Packets.Builder.busStopList(busMap.get(bus_id)));
                    socket.getOutputStream().flush();
                }
                if (data.startsWith("2")) {
                    Packets.Reader.busPosition(data);
                }
                if (data.endsWith("\n\n")) {
                    Packets.Reader.busStopList(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        if (running) return socket.getLocalPort();
        else return -1;
    }
}
