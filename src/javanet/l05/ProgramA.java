package javanet.l05;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProgramA extends Stage {
    @FXML
    TextField field;
    ProgramA controller;

    @FXML
    void query(ActionEvent event) {
    }

    public ProgramA() {
    }

    public void init(Parent root, ProgramA controller) {
        this.controller = controller;
        setScene(new Scene(root));
        show();
    }
}
