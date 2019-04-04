package javanet.l04;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class T1ServerGUI extends Pane {
    ChartData data = new ChartData();
    private ConcurrentLinkedQueue<Pair<Integer, XYChart.Data<Number, Number>>> pending = new ConcurrentLinkedQueue<>();
    private ConcurrentHashMap<Number, Number> avgMap = new ConcurrentHashMap<>();
    Task<ObservableList<XYChart.Data<Number, Number>>> avgTask = new Task<ObservableList<XYChart.Data<Number, Number>>>() {
        @Override
        protected ObservableList<XYChart.Data<Number, Number>> call() throws Exception {
            while (true) {
                ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
                for (Map.Entry<Number, Number> entry : avgMap.entrySet()) {
                    data.add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                updateValue(data);
                Thread.sleep(1000);
            }
        }
    };

    public void put(Integer ID, Number time, Number temp) {
        pending.add(new Pair<>(ID, new XYChart.Data<>(time, temp)));
    }

    public T1ServerGUI() {
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("时间 (t/s)");
        yAxis.setLabel("温度 (T/℃)");
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        xAxis.setAnimated(false);
        xAxis.setScaleShape(false);
        lineChart.setMinWidth(1600);
        lineChart.setMinHeight(900);
        lineChart.setTitle("温度曲线图");
        lineChart.dataProperty().bind(data.valueProperty());
        data.addSeries(-1);
        data.list.get(data.posMap.get(-1)).dataProperty().bind(avgTask.valueProperty());
        new Thread(avgTask).start();
        new Thread(data).start();
        this.getChildren().add(lineChart);
    }

    class ChartData extends Task<ObservableList<XYChart.Series<Number, Number>>> {
        protected ObservableList<XYChart.Series<Number, Number>> list = FXCollections.observableArrayList();
        protected HashMap<Integer, Integer> posMap = new HashMap<>();

        private void addSeries(Integer ID) {
            ObservableList<XYChart.Series<Number, Number>> nl = FXCollections.observableArrayList(list);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(String.valueOf(ID));
            if (ID == -1) series.setName("Avg.");
            posMap.put(ID, nl.size());
            nl.add(series);
            list = nl;
        }

        private void update(int maxAmount) {
            if (!pending.isEmpty()) {
                for (int i = 0; i < maxAmount; i++) {
                    Pair<Integer, XYChart.Data<Number, Number>> dataPair = pending.poll();
                    if (dataPair == null) break;
                    if (!posMap.containsKey(dataPair.getKey())) addSeries(dataPair.getKey());
                    list.get(posMap.get(dataPair.getKey())).getData().add(dataPair.getValue());
                    if (!avgMap.containsKey(dataPair.getValue().getXValue()))
                        avgMap.put(dataPair.getValue().getXValue(), dataPair.getValue().getYValue());
                    avgMap.put(dataPair.getValue().getXValue(), (avgMap.get(dataPair.getValue().getXValue()).doubleValue() + dataPair.getValue().getYValue().doubleValue()) / 2f);
                }
            }
        }

        @Override
        protected ObservableList<XYChart.Series<Number, Number>> call() throws Exception {
            while (true) {
                update(10);
                updateValue(list);
                Thread.sleep(1000);
            }
        }
    }
}