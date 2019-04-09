package javanet.l05;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

//1．	编写一个公交车查询系统，该系统由A、B、C三部分组成，
// 其中A为公交车查询客户端程序；
// B为公交查询服务程序，负责响应来自A的查询并接收由公交车模拟程序发送过来的当前到站信息；
// C为公交车模拟程序，能够模拟多辆公交车发送当前到站信息。
public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static void Exit() {
        System.out.println(Thread.currentThread().getName() + " called Exit()");
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        Button A = new Button("程序A:为公交车查询客户端程序(客户端)");
        Button B = new Button("程序B:公交查询服务程序(服务器)");
        Button C = new Button("程序C:公交车模拟程序(公交)");
        A.setOnAction(event -> {
            primaryStage.hide();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientUI.fxml"));
                Parent root = loader.load();
                ((ProgramA) loader.getController()).init(root, loader.getController());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        B.setOnAction(event -> {
            primaryStage.hide();
            new ProgramB();
        });
        C.setOnAction(event -> {
            primaryStage.hide();
            new ProgramC();
        });
        vBox.getChildren().addAll(A, B, C);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));
        primaryStage.setScene(new Scene(vBox));
        primaryStage.setTitle("Launcher");
        primaryStage.show();
    }
}
