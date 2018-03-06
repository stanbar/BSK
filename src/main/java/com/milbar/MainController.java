package com.milbar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label lblClassName;

    @FXML
    private void initialize() {
        lblClassName.setText(this.getClass().getCanonicalName());
    }
}
