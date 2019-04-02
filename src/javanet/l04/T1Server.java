package javanet.l04;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class T1Server extends Application {
    private static T1ServerGUI GUI = null;
    private static long t0 = Calendar.getInstance().getTime().getTime();

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("Server Thread");
            ExecutorService service = Executors.newFixedThreadPool(32);
            while (GUI == null) System.out.println("Waiting GUI");
            System.out.println("GUI started");
            try {
                DatagramSocket socket = new DatagramSocket(65534);
                while (true) {
                    byte[] bytes = new byte[16];
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    System.out.println("Waiting...");
                    socket.receive(packet);
                    service.submit(() -> ServerThread(packet.getData(), packet.getPort()));
                }
            } catch (SocketException e) {
                System.out.println("Can not bind port!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
        launch(args);
    }

    private static void ServerThread(byte[] data, int ClientPort) {
        try {
            System.out.println("Received Data,ClientPort : " + ClientPort);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            Number time = (new Date(buffer.getLong()).getTime() - t0) / 1000;
            Number temp = buffer.getDouble();
            GUI.data.put(ClientPort, time, temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GUI = new T1ServerGUI();
        primaryStage.setScene(new Scene(GUI));
        primaryStage.show();
        primaryStage.setOnHiding(event -> System.exit(0));
        primaryStage.setOnHidden(event -> System.exit(0));
    }
}
