package javanet.l05;

import com.sun.istack.internal.Nullable;
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
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

    @FXML
    void query(ActionEvent event) {
        String str = field.getText();
        if (initialized)
            service.submit(() -> {
                LinkedList<String> list = networkThread.queryStopList(str.trim());
                Platform.runLater(() -> {
                    for (String s : list) {
                        addStop(s);
                    }
                });
            });
    }

    public ProgramA() {
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

        }

        @Nullable
        @SuppressWarnings("unchecked")
        protected LinkedList<String> queryStopList(String bus_id) {
            LinkedList<String> list = null;
            try {
                socket.getOutputStream().write(Packets.Builder.clientQuery(bus_id));
                socket.getOutputStream().flush();
                byte[] bytes = new byte[10240];
                int readBytes = socket.getInputStream().read(bytes);
                list = (LinkedList<String>) Packets.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }
    }
}
