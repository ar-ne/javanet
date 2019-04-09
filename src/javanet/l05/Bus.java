package javanet.l05;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.LinkedList;

public class Bus {
    LinkedList<String> stops;
    /**
     * 公交位置，未发车则pos==-1
     */
    int pos = -1;
    int id;
    Task<Integer> positionTask;
    @FXML
    Label text1;
    @FXML
    Rectangle mark1;

    public void setStops(LinkedList<String> stops) {
        this.stops = stops;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText1(String string) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
            builder.append(string.charAt(i)).append("\n");
        text1.setText(builder.toString());
    }

    public void toggleMark() {
        mark1.setVisible(!mark1.isVisible());
    }

    public void setPositionTask() {
        positionTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int old = pos;
                updateValue(pos);
                while (true) {
                    if (old != pos) {
                        old = pos;
                        updateValue(pos);
                    }
                    Thread.sleep(1000);
                }
            }
        };
    }
}
