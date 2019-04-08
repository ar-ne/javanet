package javanet.c04.t3;

import com.rits.cloning.Cloner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
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
//cloning库依赖于objenesis，http://objenesis.org
public class t1 extends Application {
    private static DatagramSocket socket;
    private static ConcurrentHashMap<Integer, Client> clients;
    private static HashMap<String, String> quesionts;
    private static ExecutorService service = Executors.newFixedThreadPool(32);


    public static void main(String[] args) throws SocketException {
        socket = new DatagramSocket(0);
        clients = new ConcurrentHashMap<>();
        quesionts = loadQuestion();
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

    private static HashMap<String, String> loadQuestion() {
        HashMap<String, String> map = new HashMap<>();
        File f = new File("questions.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            int x = 0;
            String q = "";
            while ((line = reader.readLine()) != null) {
                if (x == 0 && q.length() == 0) q = line;
                if (x == 1) {
                    map.put(q, line);
                    q = "";
                }
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void response(DatagramSocket socket, byte[] data, int length, InetAddress host, int port) {
        try {
            if (!clients.containsKey(port)) clients.put(port, new Client(quesionts));
            Client client = clients.get(port);
            ByteBuffer buffer32 = ByteBuffer.allocate(32).putInt(client.score);
            switch (length) {
                case 32:
                    if (!client.started) {
                        client.started = true;
                        sendNewQeustion(socket, host, port, client, buffer32);
                    } else socket.send(new DatagramPacket(buffer32.array(), buffer32.array().length, host, port));
                    break;
                case 10240:
                    client.judge(new String(data));
                    sendNewQeustion(socket, host, port, client, buffer32);
                    break;
                default:
                    System.out.println("wrong packet from " + port);
            }

        } catch (Exception ignored) {
        }
    }

    private static void sendNewQeustion(DatagramSocket socket, InetAddress host, int port, Client client, ByteBuffer buffer32) throws IOException {
        if (client.questionLeft() <= 0)
            socket.send(new DatagramPacket(buffer32.array(), buffer32.array().length, host, port));
        else {
            ByteBuffer buffer = ByteBuffer.allocate(10240).put(client.getNewQuestion().getBytes());
            socket.send(new DatagramPacket(buffer.array(), buffer.array().length, host, port));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent x = loader.load();
        ((ClientCon) loader.getController()).init(x, InetAddress.getLocalHost(), socket.getLocalPort());
        ((ClientCon) loader.getController()).setOnHiding(event -> System.exit(0));
        ((ClientCon) loader.getController()).setOnHidden(event -> System.exit(0));
    }

    static class Client {
        int score = 0;
        HashMap<String, String> userQuestion;
        String currentQuestion;
        boolean started = false;

        Client(HashMap<String, String> questions) {
            Cloner cloner = new Cloner();
            this.userQuestion = cloner.deepClone(questions);
        }

        String getNewQuestion() {
            currentQuestion = userQuestion.get(userQuestion.keySet().toArray(new String[0])[(int) (Math.random() * userQuestion.size())]);
            return currentQuestion;
        }

        void judge(String answer) {
            if (answer.equals(userQuestion.get(currentQuestion))) score++;
            userQuestion.remove(currentQuestion);
        }

        int questionLeft() {
            return userQuestion.size();
        }
    }
}
