package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.exceptions.InstanceInitializeException;
import com.milbar.logic.exceptions.LoginException;
import com.milbar.logic.exceptions.RegisterException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.LoginManager;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.security.wrappers.Password;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginWindowController extends JavaFXController implements JavaFXWindowsListener {
    
    private static Logger log = LoggerFactory.getLogger(LoginWindowController.class);
    private static final int AUTOCOMPLETION_DELAY = 30;
    
    private MainWindowController parentController;
    private Set<String> openedWindows = new HashSet<>();
    
    private Stage stage;
    private boolean loginAllowed = true;
    private LoginManager loginManager;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    public PasswordField passwordFieldRepeat;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void initialize() {
        try {
            loginManager = new LoginManager();
            List<String> usersList = loginManager.getUsersList();
            if (usersList != null && usersList.size() > 0)
                TextFields.bindAutoCompletion(usernameField, usersList).setDelay(AUTOCOMPLETION_DELAY);
        } catch (InstanceInitializeException e) {
            log.log(Level.SEVERE, "Failed to initialize login window.");
        }
    }
    
    @FXML
    public void cancelButtonClicked() {
        closeWindow();
    }
    
    @FXML
    synchronized public void loginButtonClicked() {
        if (!isLoginAllowed()) {
            errorLabel.setText("Logging in is blocked right now.");
            return;
        }
        if (areCredentialsEntered()) {
            try {
                handleUserLoginEvent(usernameField.getText(), passwordField.getText());
            } catch (LoginException e) {
                log.log(Level.SEVERE, e.getMessage());
                errorLabel.setText(e.getMessage());
            }
        } else
            errorLabel.setText("There are empty fields.");
    }
    
    private void handleUserLoginEvent(String username, String password) throws LoginException {
        Password wrappedPassword = new Password(password.toCharArray());
        SessionToken sessionToken = loginManager.login(username, wrappedPassword);
        parentController.loginUser(sessionToken);
        closeWindow();
    }
    
    @FXML
    public void registerButtonClicked() {
        lockLogin();
        openRegisterWindow();
    }
    
    private void openRegisterWindow() {
        if (openedWindows.add(RegisterWindow.class.getCanonicalName())) {
            try {
                RegisterWindow registerWindow = new RegisterWindow(this);
            } catch (IOException e) {
                e.printStackTrace();
                writeToErrorLabel("Failed to open register window.");
            }
        }
        else
            writeToErrorLabel("Register window is already open!");
    }
    
    private void writeToErrorLabel(String msg) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(Color.RED);
    }
    
    private void lockLogin() {
        loginAllowed = false;
    }
    
    private void unlockLogin() {
        loginAllowed = true;
    }
    
    @Override
    public synchronized void closeWindow() {
        parentController.windowClosed(LoginWindow.class.getCanonicalName());
        
        stage.close();
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        this.parentController = (MainWindowController)parentController;
    }
    
    private boolean areCredentialsEntered() {
        return usernameField.getLength() > 0 && passwordField.getLength() > 0;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private boolean isLoginAllowed() {
        return loginAllowed;
    }
    
    void registerNewUser(String username, String password) throws RegisterException {
        Password wrappedPassword = new Password(password.toCharArray());
        loginManager.register(username, wrappedPassword);
    }
    
    @Override
    public void windowClosed(String callerClassName) {
        if (!openedWindows.remove(callerClassName))
            throw new UnexpectedWindowEventCall("Class name: " + callerClassName);
        
        unlockLogin();
    }
}