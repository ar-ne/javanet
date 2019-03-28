/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

/*
 * 2019.
 * @Author 16201235@stu.nchu.edu.cn
 */

package javanet.c02.t3;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//利用Java FX、定时器、多线程完成一个Java动画程序，要求该程序能够随机生成10不同颜色、不同大小的圆，每个圆能够颜色渐变、直线运动以及碰撞反弹。
public class t extends Application {
    Pane pane = new Pane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(pane, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        for (int i = 0; i < 20; i++) {
            pane.getChildren().add(new AutoMoveCircle(800, 800, Math.random() * 20 + 100, Math.random() * 40 + 100, Math.random() * 70 + 30, Math.random(), (long) (Math.random() * 7 + 90)));
        }
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    class AutoMoveCircle extends Circle {
        double paneX, paneY;
        double initX, initY;
        double radius, slope;
        double edgePadding = 1;
        boolean stop = false;
        long updateLag;
        ExecutorService service = Executors.newFixedThreadPool(3);
        Task<Double> taskX = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                System.out.println(Thread.currentThread().getName() + " => serviceX");
                double u = initX;
                boolean forward = true;
                while (!stop) {
                    if (forward) u++;
                    else u--;
                    if (u + radius >= paneX - edgePadding)
                        forward = false;
                    if (u - radius <= edgePadding)
                        forward = true;
                    updateValue(u);
                    Thread.sleep(updateLag);
                }
                return null;
            }
        };
        Task<Double> taskY = new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                System.out.println(Thread.currentThread().getName() + " => serviceY");
                double u = initY;
                boolean forward = true;
                while (!stop) {
                    if (forward) u += slope;
                    else u -= slope;
                    if (u + radius >= paneX - edgePadding)
                        forward = false;
                    if (u - radius <= edgePadding)
                        forward = true;
                    updateValue(u);
                    Thread.sleep(updateLag);
                }
                return null;
            }
        };

        Task<Paint> paintTask = new Task<Paint>() {
            @Override
            protected Paint call() throws Exception {
                System.out.println(Thread.currentThread().getName() + " => paintService");
                double[] rgba = {Math.random(), Math.random(), Math.random(), Math.random()};
                double d = 0.005f;
                while (!stop) {
                    for (int i = 0; i < rgba.length; i++) {
                        if (rgba[i] >= 0.99f) d *= -1.0f;
                        if (rgba[i] <= 0.4f) d *= -1.0f;
                        if (Math.random() > 0.5f) rgba[i] += d;
                        if (rgba[i] > 1.0f) rgba[i] = 1.0f;
                        if (rgba[i] < 0f) rgba[i] = 0f;
                    }
                    updateValue(Color.color(rgba[0], rgba[1], rgba[2], rgba[3]));
                    Thread.sleep(updateLag);
                }
                return null;
            }
        };

        AutoMoveCircle(double paneX, double paneY, double centerX, double centerY, double radius, double slope, long speed) {
            super(centerX, centerY, radius);
            this.paneX = paneX;
            this.paneY = paneY;
            this.radius = radius;
            this.slope = slope;
            this.updateLag = 101 - speed;
            this.initX = centerX;
            this.initY = centerY;
            pre_init();
            post_init();
        }

        void pre_init() {
            centerXProperty().bind(taskX.valueProperty());
            centerYProperty().bind(taskY.valueProperty());
            fillProperty().bind(paintTask.valueProperty());
        }

        void post_init() {
            service.submit(taskX);
            service.submit(taskY);
            service.submit(paintTask);
        }

        @Override
        protected void finalize() throws Throwable {
            stop = true;
            service.shutdown();
            super.finalize();
        }
    }
}
