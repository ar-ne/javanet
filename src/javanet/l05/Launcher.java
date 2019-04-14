package javanet.l05;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//1．	编写一个公交车查询系统，该系统由A、B、C三部分组成，
// 其中A为公交车查询客户端程序；
// B为公交查询服务程序，负责响应来自A的查询并接收由公交车模拟程序发送过来的当前到站信息；
// C为公交车模拟程序，能够模拟多辆公交车发送当前到站信息。
public class Launcher extends Application {
    private static final boolean signalApplication = false;
    private static ProgramB programB = null;

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
            if (signalApplication) primaryStage.hide();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientUI.fxml"));
                Parent root = loader.load();
                ServerInfoDialog d;
                if (programB != null) d = new ServerInfoDialog("" + programB.getTCPport());
                else d = new ServerInfoDialog();
                d.showDialog(() -> {
                    try {
                        ((ProgramA) loader.getController()).init(root, loader.getController(), d.getHost(), d.getPort());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        B.setOnAction(event -> {
            if (signalApplication) primaryStage.hide();
            B.setDisable(true);
            programB = new ProgramB();
        });
        C.setOnAction(event -> {
            if (signalApplication) primaryStage.hide();
            BusInfoDialog dialog;
            if (programB != null)
                dialog = new BusInfoDialog(String.valueOf(programB.getUDPport()));
            else dialog = new BusInfoDialog();
            dialog.showDialog(() -> new ProgramC(dialog.host.getText(), dialog.getPort(), dialog.getBusID(), dialog.getMaxStop()));
        });
        vBox.getChildren().addAll(A, B, C);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));
        if (!signalApplication) {
            primaryStage.setOnHiding(event -> System.exit(0));
            primaryStage.setOnHidden(event -> System.exit(0));
        }
        primaryStage.setScene(new Scene(vBox));
        primaryStage.setTitle("Launcher");
        primaryStage.show();
    }

    class BusInfoDialog extends ServerInfoDialog {
        TextField busid = new TextField(), stops = new TextField();

        BusInfoDialog(String portNum) {
            super(portNum);
            init();
        }

        BusInfoDialog() {
            init();
        }

        void init() {
            busid.setPromptText("线路号");
            stops.setPromptText("站点数量");
            v.getChildren().clear();
            v.getChildren().addAll(busid, stops, host, port, ok);
        }

        String getBusID() {
            return busid.getText().trim();
        }

        int getMaxStop() {
            return Integer.parseInt(stops.getText().trim());
        }
    }

    class ServerInfoDialog extends Stage {
        TextField host = new TextField("127.0.0.1"), port = new TextField();
        Button ok = new Button("确定");
        VBox v = new VBox();

        ServerInfoDialog(String portNum) {
            host.setPromptText("host");
            port.setPromptText("port");
            port.setText(portNum);
            v.getChildren().addAll(host, port, ok);
            setScene(new Scene(v));
        }

        ServerInfoDialog() {
            host.setPromptText("host");
            port.setPromptText("port");
            v.getChildren().addAll(host, port, ok);
            setScene(new Scene(v));
        }

        void showDialog(Runnable callback) {
            show();
            requestFocus();
            ok.setOnAction(event -> {
                close();
                callback.run();
            });
        }

        public InetAddress getHost() throws UnknownHostException {
            return InetAddress.getByName(host.getText().trim());
        }

        public int getPort() {
            return Integer.parseInt(port.getText().trim());
        }
    }
}
