package javanet.c04.t3;

import com.rits.cloning.Cloner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//1.	基于UDP协议编写一个考试程序，其要求如下：
//每个客户端连接上服务器之后，服务器将随机向客户端发送考题，每次一题；
//每答完一题客户端可以选择继续答题或结束答题；
//结束答题后，客户端可以查看自己的成绩；
//客户端基于Java FX实现，服务端基于多线程技术实现，考试题存储在文件当中。

//使用额外额cloning库 https://github.com/kostaskougios/cloning
public class t1 extends Application {
    private static DatagramSocket socket;
    private static ConcurrentHashMap<Integer, Client> clients;
    private static HashMap<String, String> quesionts;
    private static ExecutorService service = Executors.newFixedThreadPool(32);


    public static void main(String[] args) throws SocketException {
        socket = new DatagramSocket(0);
        clients = new ConcurrentHashMap<>();
        new Thread(() -> {
            while (true) {
                try {
                    byte[] bytes = new byte[10240];
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                    socket.receive(packet);
                    service.submit(() -> response(socket, packet.getData(), packet.getLength(), packet.getAddress(), packet.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        launch(args);
    }

    private static void response(DatagramSocket socket, byte[] data, int length, InetAddress host, int port) {
        try {
            if (!clients.containsKey(port)) clients.put(port, new Client(quesionts));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent x = loader.load();
        ((ClientCon) loader.getController()).init(x, InetAddress.getLocalHost(), socket.getPort());
    }

    static class Client {
        int score = 0;
        HashMap<String, String> userQuestion;
        String currentQuestion;

        Client(HashMap<String, String> questions) {
            Cloner cloner = new Cloner();
            this.userQuestion = cloner.deepClone(questions);
        }

        String getNewQuestion() {
            currentQuestion = userQuestion.get(userQuestion.keySet().toArray(new String[0])[(int) (Math.random() * userQuestion.size())]);
            return currentQuestion;
        }
    }
}
