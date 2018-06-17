package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.exceptions.RegisterException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.logging.Logger;

public class RegisterWindowController extends JavaFXController {
    
    private static Logger log = LoggerFactory.getLogger(RegisterWindowController.class);
    
    private LoginWindowController parentController;
    
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
        if (userNameField.getLength() > 0 && passwordField.getLength() > 0 && passwordFieldRepeat.getLength() > 0) {
            if (passwordField.getText().equals(passwordFieldRepeat.getText()))
                registerUser(userNameField.getText(), passwordField.getText());
            else
                inputValidationFailed("Password does not repeat correctly.");
        }
        else
            inputValidationFailed("Password and username cannot be empty.");
    }
    
    private void registerUser(String username, String password) {
        try {
            clearInputFields();
            parentController.registerNewUser(username, password);
            closeWindow();
        } catch (RegisterException e) {
            inputValidationFailed(e.getMessage());
        }
    }
    
    private void clearInputFields() {
        userNameField.setText("");
        passwordField.setText("");
        passwordFieldRepeat.setText("");
    }
    
    private void inputValidationFailed(String msg) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(Color.RED);
    }
    
    @FXML
    public void cancelButtonClicked() {
        closeWindow();
    }
    
    @Override
    public void closeWindow() {
        parentController.windowClosed(RegisterWindow.class.getCanonicalName());
        stage.close();
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        this.parentController = (LoginWindowController) parentController;
    }
}
