package javanet.l05;

import com.rits.cloning.Cloner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;

public class Bus {
    LinkedList<String> stops;
    /**
     * 公交位置，未发车则pos==-1
     */
    int pos = -1;
    String id;
    Task<Integer> positionTask;
    @FXML
    Label text1, num;
    @FXML
    Rectangle mark1;

    public void setStops(LinkedList<String> stops) {
        Cloner cloner = new Cloner();
        this.stops = cloner.deepClone(stops);
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText1(String string) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
            builder.append(string.charAt(i)).append("\n");
        text1.setText(builder.toString());
    }

    public void init(int number, String stopName) {
        num.setText(number + "");
        setText1(stopName);
        toggleMark();
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

    @Override
    public String toString() {
        return "Bus{" +
                "id='" + id + '\'' +
                ", pos=" + pos +
                ", stops=" + stops +
                ", positionTask=" + positionTask +
                ", text1=" + text1 +
                ", num=" + num +
                ", mark1=" + mark1 +

                '}';
    }
}
