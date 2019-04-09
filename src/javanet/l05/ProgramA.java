package javanet.l05;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProgramA extends Stage {
    @FXML
    TextField field;

    @FXML
    void query(ActionEvent event) {
        System.out.println("event tests");
    }
}
