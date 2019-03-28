/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t3;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

//1.	将练习3.2中第3题的客户端改为图形界面，同时利用多线程技术使其服务端能够同时服务多个客户端。
public class t3 extends Application {
    static AtomicInteger port = new AtomicInteger(0);
    static ConcurrentLinkedQueue<String> serverMsgQueue = new ConcurrentLinkedQueue<>();
    static ObservableList<SocketRecord> observableList = FXCollections.observableArrayList();
    @FXML
    TextArea msg;
    @FXML
    TableColumn<SocketRecord, String> list;
    private Task<String> msgTask = new Task<String>() {
        @Override
        protected String call() throws Exception {
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                while (!serverMsgQueue.isEmpty()) {
                    stringBuilder.append(serverMsgQueue.poll());
                    stringBuilder.append("\n");
                    updateValue(stringBuilder.toString());
                }
                Thread.sleep(15);
            }
        }
    };

    public static void main(String[] args) {
        boolean stop = false;
        Runnable server = () -> {
            try {
                String prefix = "";
                ServerSocket serverSocket = new ServerSocket(port.get());
                port.set(serverSocket.getLocalPort());
                serverMsgQueue.add(prefix + "Listening on port:" + port.get());
                new Thread(() -> launch(args)).start();
                while (!stop) {
                    SocketRecord record = new SocketRecord(serverSocket.accept());
                    observableList.add(record);
                    new Thread(() -> serverSocketThread(record), "" + record.socket.getPort()).start();
                }
                serverSocket.close();
                serverMsgQueue.add(prefix + "server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(server, "Server").start();
    }

    private static void serverSocketThread(SocketRecord record) {
        String prefix = "[ServerThread=>" + Thread.currentThread().getName() + "]";
        serverMsgQueue.add(prefix + "Accept income connection:" + record.socket);
        try {
            while (!record.socket.isClosed()) {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                int r = record.socket.getInputStream().read(buffer.array());
                if (r == -1) {
                    serverMsgQueue.add(prefix + "Socket closed!");
                    break;
                }
                int s = buffer.getInt();
                serverMsgQueue.add(prefix + "Received " + r + " bytes, value is " + s);
                buffer = ByteBuffer.allocate(8 * s);
                r = record.socket.getInputStream().read(buffer.array());
                serverMsgQueue.add(prefix + "Received " + r + " bytes");
                List<Double> list = new ArrayList<>();
                for (int i = 0; i < s; i++)
                    list.add(buffer.getDouble());
                record.socket.getOutputStream().write(e3.t2.t3.results(list));
                serverMsgQueue.add(prefix + "Response send");
            }
        } catch (SocketException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverMsgQueue.add(prefix + "Closed");
            observableList.remove(record);
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("t3s.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Server port : " + port.get());
        primaryStage.show();
        primaryStage.setOnHiding((e) -> System.exit(0));
        primaryStage.setOnHidden((e) -> System.exit(0));
    }

    @FXML
    private void initialize() {
        msg.textProperty().bind(msgTask.valueProperty());
        new Thread(msgTask).start();
        list.setCellValueFactory(e -> e.getValue().valProperty());
        list.getTableView().setItems(observableList);
    }

    @FXML
    void newClient() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("t3c.fxml"));
        Parent parent = loader.load();
        t3c controller = loader.getController();
        controller.init(port.get(), parent);
    }
}

class SocketRecord {
    Socket socket;
    private StringProperty val = new SimpleStringProperty();

    SocketRecord(Socket socket) {
        this.val.set(String.valueOf(socket.getPort()));
        this.socket = socket;
    }

    public StringProperty valProperty() {
        return val;
    }
}