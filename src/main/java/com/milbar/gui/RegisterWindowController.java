package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterWindowController extends JavaFXController {
    
    private static Logger log = LoggerFactory.getLogger(RegisterWindowController.class);
    
    private LoginWindowController parentController;
    
    private String username, password, passwordRepeat;
    
    @FXML
    public TextField userNameField;
    
    @FXML
    public PasswordField passwordField;

    @FXML
    public PasswordField passwordFieldRepeat;
    
    @FXML
    public Label errorLabel;
    
    @FXML
    public void registerButtonClicked() {
    
    }

    @FXML
    public void userNameEntered() {
        username = userNameField.getText();
        log.log(Level.INFO, "User entered username {0}", username);
    }

    @FXML
    public void passwordEntered() {
        password = passwordField.getText();
    
        //todo delete this on release
        log.log(Level.INFO, "User entered password {0}", password);
    }
    
    @FXML
    public void passwordRepeatEntered() {
        passwordRepeat = passwordFieldRepeat.getText();
    
        //todo delete this on release
        log.log(Level.INFO, "User repeated password {0}", passwordRepeat);
    }
    
    private void refreshInputData() {
        userNameEntered();
        passwordEntered();
        passwordRepeatEntered();
    }
    
    @FXML
    public void cancelButtonClicked() {
    
    }
    
    @Override
    public void closeWindow() {
    
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        this.parentController = (LoginWindowController) parentController;
    }
    
}
