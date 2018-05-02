package com.milbar.gui;

import com.milbar.logic.FileEncryptionJob;
import com.milbar.logic.login.UserCredentials;
import com.milbar.logic.encryption.type.BlockEncryptionTypes;
import com.milbar.logic.exceptions.IllegalEventSourceException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.stasbar.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements JavaFXWindowsListener {
    
    private final static int THREADS_POOL_SIZE = 4;
    private final static BlockEncryptionTypes DEFAULT_BLOCK_ENCRYPTION_TYPE = BlockEncryptionTypes.ElectronicCodebook;
    private final static String DEFAULT_RADIO_BUTTON_NAME = "radioButton";
    
    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_POOL_SIZE);
    private ObservableList<FileEncryptionJob> tableElementsList = FXCollections.observableArrayList();
    private BlockEncryptionTypes selectedBlockEncryptionType = DEFAULT_BLOCK_ENCRYPTION_TYPE;
    private List<File> files;
    private boolean filesSelected = false;
    private int filesSelectedAmount = -1;
    private FileChooser fileChooser = new FileChooser();
    private Set<String> openedWindows = new HashSet<>();
    private LoginWindow loginWindow;
    private UserCredentials userCredentials;
    
    @FXML
    TableColumn<FileEncryptionJob, String> imageNameColumn;
    @FXML
    TableColumn<FileEncryptionJob, Double> progressColumn;
    @FXML
    TableColumn<FileEncryptionJob, String> statusColumn;
    @FXML
    TableView<FileEncryptionJob> filesTable;
    
    @FXML
    Pane mainPane;
    @FXML
    Label logLabel;

    
    @FXML
    private void initialize() {
        initializeFileChooser();
        refreshTable();
    }
    
    private void initializeFileChooser() {
        fileChooser.setTitle("Select files to encrypt.");
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG images", "*.jpg"));
    }
    
    @FXML
    void menuBarActionCloseApplication() {
    
    }
    
    @FXML
    void chooseFilesButtonClicked() {
        files = fileChooser.showOpenMultipleDialog(null);
        if (files == null) {
            filesSelected = false;
            return;
        }
        
        filesSelectedAmount = files.size();
        filesSelected = filesSelectedAmount > 0;
        writeToLogLabel("Selected " + filesSelectedAmount + " files for encryption.");
        createFileEncryptionJobsList();
    }
    
    @FXML
    void encryptFilesButtonClicked() {
        createFileEncryptionJobsList();
        
        if (filesSelected) {
            writeToLogLabel("Starting encryption of " + filesSelectedAmount + " files.");
            tableElementsList.forEach(task -> executor.submit(task));
            
        }
    }
    
    private void createFileEncryptionJobsList() {
        if (filesSelected) {
            tableElementsList.clear();
            files.forEach(file -> tableElementsList.add(new FileEncryptionJob(file)));
        }
        refreshTable();
    }
    
    private void refreshTable() {
        imageNameColumn.setCellValueFactory( //nazwa pliku
                p -> new SimpleStringProperty(p.getValue().getFile().getName()));
        
        statusColumn.setCellValueFactory( //status przetwarzania
                p -> p.getValue().getStatusProperty());
        
        progressColumn.setCellFactory( //wykorzystanie paska postępu
                ProgressBarTableCell.forTableColumn());
        
        progressColumn.setCellValueFactory( //postęp przetwarzania
                p -> p.getValue().getProgressProperty().asObject());
        
        filesTable.setItems(tableElementsList);
    }
    
    @FXML
    public void loginMenuBarClicked() {
        if (openedWindows.add(LoginWindow.class.getSimpleName())) {
            try {
                loginWindow = new LoginWindow(this);
            } catch (IOException e) {
                e.printStackTrace();
                writeToLogLabel("Failed to open login window.");
            }
        }
        else {
            writeToLogLabel("Login window is already opened!");
        }
    }
    
    @FXML
    public void logoutMenuBarClicked() {
        logoutCurrentUser();
    }
    
    private void logoutCurrentUser() {
        userCredentials.destroy();
        userCredentials = null;
        writeToLogLabel("Successfully logged out.");
    }
    
    @FXML
    public void showAboutMenuBarClicked() {
        openNewWindow("fxml/AboutWindow.fxml", "About");
    }
    
    @FXML
    public void radioButtonSelected(ActionEvent event) throws IllegalEventSourceException {
        RadioButton rb = (RadioButton)event.getSource();
        
        switch (getRadioButtonNumber(rb)) {
            case "1": selectedBlockEncryptionType = BlockEncryptionTypes.ElectronicCodebook; break;
            case "2": selectedBlockEncryptionType = BlockEncryptionTypes.CipherBlockChaining; break;
            case "3": selectedBlockEncryptionType = BlockEncryptionTypes.CipherFeedbackMode; break;
            case "4": selectedBlockEncryptionType = BlockEncryptionTypes.OutputFeedbackMode; break;
            default: throw new IllegalEventSourceException(rb.getId());
        }
    }
    
    @Override
    public void windowClosed(String callerClassName) {
        if (!openedWindows.remove(callerClassName))
            throw new UnexpectedWindowEventCall("Class name: " + callerClassName);
    }
    
    private void writeToLogLabel(String log) {
        this.logLabel.setText(log);
        Logger.info(log);
    }
    
    private String getRadioButtonNumber(RadioButton radioButton) {
        String radioButtonId = radioButton.getId();
        String radioButtonNameAndNumber[] = radioButtonId.split(DEFAULT_RADIO_BUTTON_NAME);
        return radioButtonNameAndNumber.length > 1 ? radioButtonNameAndNumber[1] : "-1";
    }
    
    private void openNewWindow(String fxmlPath, String windowTitle) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(windowTitle);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void loginUser(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
        writeToLogLabel("Logged in as user: " + userCredentials.getUsername());
    }
}