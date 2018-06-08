package com.milbar.gui.helpers;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogLabel {
    
    private Label label;
    private Logger logger;
    
    public LogLabel(Label label, Logger logger) {
        this.label = label;
        this.logger = logger;
    }
    
    public void writeInfo(String log) {
        write(log, Level.INFO);
    }
    
    public void writeWarning(String log) {
        write(log, Level.WARNING);
    }
    
    public void writeError(String log) {
        write(log, Level.SEVERE);
    }
    
    private void write(String log, Level level) {
        Platform.runLater(() -> {
            label.setText(log);
            label.setStyle(getColorStyle(level));
        });
        logger.log(level, log);
    }
    
    private String getColorStyle(Level level) {
        String result = "-fx-text-fill: ";
        if (level == Level.SEVERE)
            return result + "red";
        else
            return result + "black";
    }
}
