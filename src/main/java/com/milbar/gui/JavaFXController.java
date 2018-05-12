package com.milbar.gui;

import javafx.stage.Stage;

public abstract class JavaFXController {
    protected Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public abstract void closeWindow();
    
    public abstract void setParentController(JavaFXController parentController);
}
