package com.milbar.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public abstract class JavaFXWindow {
    protected JavaFXController controller;
    protected FXMLLoader fxmlLoader;
    protected Stage stage;
    protected JavaFXWindowsListener parentListener;
    
    public JavaFXWindow(JavaFXWindowsListener parentListener) {
        this.parentListener = parentListener;
    }
    
    public JavaFXController getController() {
        return controller;
    }
    
    void setIcon() {
        stage.getIcons().add(new Image("images/logo.png"));
    }
}
