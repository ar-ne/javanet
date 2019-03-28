/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c03.t3;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class t3c extends Stage {
    @FXML
    TextArea msg, input;
    @FXML
    TextField sum, avg, variance;
    @FXML
    Button btn;
    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private Socket socket;
    private Task<String> msgTask = new Task<String>() {
        @Override
        protected String call() throws Exception {
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                while (!queue.isEmpty()) {
                    stringBuilder.append(queue.poll());
                    stringBuilder.append("\n");
                    updateValue(stringBuilder.toString());
                }
                Thread.sleep(10);
            }
        }
    };

    void init(int port, Parent root) throws IOException {
        setScene(new Scene(root));
        socket = new Socket(InetAddress.getLocalHost(), port);
        queue.add("Connected socket => " + socket);
        setTitle("Local port : " + socket.getLocalPort());
        setResizable(false);
        msg.setEditable(false);
        sum.setEditable(false);
        avg.setEditable(false);
        variance.setEditable(false);
        msg.textProperty().bind(msgTask.valueProperty());
        new Thread(msgTask).start();
        show();
        setOnHidden(this::close);
        setOnCloseRequest(this::close);
    }

    private void disable() {
        btn.setDisable(true);
        input.setEditable(false);
    }

    private void enable() {
        btn.setDisable(false);
        input.setEditable(true);
    }

    @FXML
    void onSubmit() {
        disable();
        new Thread(() -> {
            try {
                if (input.getText().isEmpty() || input.getText().trim().length() == 0) {
                    queue.add("Please input data");
                    Platform.runLater(this::enable);
                    return;
                }
                String[] parts = input.getText().trim().replace("\n", " ").replace("\r", " ").split(" ");
                List<Double> requestList = new ArrayList<>();
                for (int i = 0; i < parts.length; i++)
                    try {
                        double v = Double.parseDouble(parts[i]);
                        requestList.add(v);
                    } catch (NumberFormatException x) {
                        queue.add("Ignored : " + parts[i] + " cause Not Valid double in [" + i + "]");
                    }
                if (requestList.size() == 0) {
                    queue.add("Nothing to send!");
                    Platform.runLater(this::enable);
                    return;
                }
                ByteBuffer buffer = ByteBuffer.allocate(8 * requestList.size());
                for (Double aDouble : requestList)
                    buffer.putDouble(aDouble);
                socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(requestList.size()).array());
                queue.add("Request header send, wrote " + 4 + " bytes");
                socket.getOutputStream().write(buffer.array());
                queue.add("Request body send, wrote " + buffer.array().length + " bytes");
                buffer = ByteBuffer.allocate(8 * 3);
                int r = socket.getInputStream().read(buffer.array());
                queue.add("Response received, read " + r + " bytes,should be 24");
                ByteBuffer finalBuffer = buffer;
                Platform.runLater(() -> {
                    sum.setText(String.valueOf(finalBuffer.getDouble()));
                    avg.setText(String.valueOf(finalBuffer.getDouble()));
                    variance.setText(String.valueOf(finalBuffer.getDouble()));
                    enable();
                });
            } catch (IOException e) {
                queue.add(e.getLocalizedMessage());
            }
        }).start();
    }

    private void close(WindowEvent windowEvent) {
        try {
            socket.close();
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
