package javanet.l05;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgramA extends Stage {
    @FXML
    TextField field;
    @FXML
    HBox stopList;
    boolean initialized = false;
    HashMap<Integer, Bus> busStops = new HashMap<>();
    NetworkThread networkThread;
    ExecutorService service = Executors.newFixedThreadPool(1);
    private ProgramA controller;

    public ProgramA() {
    }

    @FXML
    void query(ActionEvent event) {
        String str = field.getText();
        if (initialized)
            service.submit(() -> {
                LinkedList<String> list = networkThread.queryStopList(str.trim());
                Platform.runLater(() -> {
                    stopList.getChildren().clear();
                    for (String s : list) {
                        addStop(s);
                    }
                });
            });
    }

    void init(Parent root, ProgramA controller, InetAddress host, int port) {
        this.controller = controller;
        networkThread = new NetworkThread(host, port);
        field.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                query(new ActionEvent());
            }
        });
        setScene(new Scene(root));
        show();
        initialized = true;
        networkThread.start();
    }

    private void setVisible(HashSet<Integer> posSet) {
        for (Map.Entry<Integer, Bus> entry : busStops.entrySet()) {
            if (posSet.contains(entry.getKey())) entry.getValue().toggleMark(true);
            else entry.getValue().toggleMark(false);
        }
    }

    private void addStop(String stopName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VLineStopMid.fxml"));
            stopList.getChildren().add(loader.load());
            busStops.put(stopList.getChildren().size() - 1, loader.getController());
            ((Bus) loader.getController()).init(stopList.getChildren().size() - 1, stopName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class NetworkThread extends Thread {
        Socket socket;
        String currentID = "";

        private NetworkThread(InetAddress host, int port) {
            try {
                socket = new Socket(host, port);
                socket.setKeepAlive(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    Thread.sleep(1000);
                    if (currentID.isEmpty()) continue;
                    HashSet<Integer> x = queryBusPosition(currentID);
                    Platform.runLater(() -> controller.setVisible(x));
                    System.out.println(Thread.currentThread().getName() + " Querying position");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Nullable
        LinkedList<String> queryStopList(String bus_id) {
            LinkedList<String> list = null;
            try {
                socket.getOutputStream().write(Packets.Builder.listQuery(bus_id));
                socket.getOutputStream().flush();
                byte[] bytes = new byte[10240];
                int readBytes = socket.getInputStream().read(bytes);
                list = Packets.Reader.busStopList(Packets.getString(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null && list.size() > 0) currentID = bus_id;
            return list;
        }

        @Nullable
        HashSet<Integer> queryBusPosition(String bus_id) {
            HashSet<Integer> set = null;
            try {
                socket.getOutputStream().write(Packets.Builder.positionQuery(bus_id));
                socket.getOutputStream().flush();
                byte[] bytes = new byte[10240];
                int readBytes = socket.getInputStream().read(bytes);
                set = Packets.Reader.busPosition(Packets.getString(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return set;
        }
    }
}
