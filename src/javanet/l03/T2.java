package javanet.l03;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

//2．	将该程序的客户端改为一个可以显示服务器时间的图形时钟界面，它每隔一秒从服务器读取服务器当前时间。
public class T2 extends Application {
    @Override
    public void start(Stage primaryStage) {
        ClockPane clock = new ClockPane();
        BorderPane borderPane = new BorderPane();

        EventHandler<ActionEvent> eventHandler = e -> {
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), 2007);
                DataInputStream stream = new DataInputStream(socket.getInputStream());
                StringBuilder builder = new StringBuilder();
                builder.append(stream.readInt()).append("年");
                builder.append((stream.readByte() & 0xff) + 1).append("月");
                builder.append(stream.readByte() & 0xff).append("日");
                int h = (stream.readByte() & 0xff);
                int m = (stream.readByte() & 0xff);
                int s = (stream.readByte() & 0xff);
                builder.append(h).append("时");
                builder.append(m).append("分");
                builder.append(s).append("秒");
                stream.close();
                socket.close();
                clock.setHour(h);
                clock.setMinute(m);
                clock.setSecond(s);
                Label lblCurrentTime = new Label(builder.toString());
                borderPane.setCenter(clock);
                borderPane.setBottom(lblCurrentTime);
                BorderPane.setAlignment(lblCurrentTime, Pos.TOP_CENTER);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        Scene scene = new Scene(borderPane, 250, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        borderPane.widthProperty().addListener(ov ->
                clock.setW(borderPane.getWidth())
        );

        borderPane.heightProperty().addListener(ov ->
                clock.setH(borderPane.getHeight())
        );
    }
}
