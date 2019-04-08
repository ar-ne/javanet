package javanet.c04.t3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientCon extends Stage {
    @FXML
    Label question;
    @FXML
    Button continued, breaks;
    @FXML
    TextArea answer;
    private DatagramSocket socket;
    private static ExecutorService service = Executors.newFixedThreadPool(1);

    void init(Parent root, InetAddress host, int port) throws IOException {
        socket = new DatagramSocket(0);
        setScene(new Scene(root));
        continued.setOnAction(event -> {
            setDisable(true);
            String answer = getAnswer();
            if (answer.length() > 0) {
                try {
                    socket.send(new DatagramPacket(answer.getBytes(), answer.getBytes().length, host, port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            service.submit(this::setQuestion);
        });
        breaks.setOnAction(event -> {
            try {
                setDisable(true);
                byte[] bytes = new byte[32];
                socket.send(new DatagramPacket(bytes, bytes.length, host, port));
                service.submit(this::setQuestion);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        show();
    }

    private void setDisable(boolean b) {
        answer.setDisable(b);
        continued.setDisable(b);
        breaks.setDisable(b);
        question.setText("等待响应...");
    }

    private String getAnswer() {
        return answer.getText();
    }

    private void setQuestion() {
        try {
            byte[] bytes = new byte[10240];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            socket.receive(packet);
            Platform.runLater(() -> {
                this.question.setText(new String(packet.getData()));
                this.setDisable(false);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
