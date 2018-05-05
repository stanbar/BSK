package com.milbar.gui;

import com.google.gson.Gson;
import com.milbar.logic.FileCipherJob;
import com.milbar.logic.encryption.Algorithm;
import com.milbar.logic.encryption.Mode;
import com.milbar.logic.exceptions.IllegalEventSourceException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.UserCredentials;
import com.stasbar.Logger;
import javafx.beans.property.SimpleObjectProperty;
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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements JavaFXWindowsListener {

    private final static int THREADS_POOL_SIZE = 4;
    private final static Mode DEFAULT_BLOCK_ENCRYPTION_MODE = Mode.ECB;
    private final static Algorithm DEFAULT_ENCRYPTION_ALGORITHM = Algorithm.DES;
    private final static String DEFAULT_RADIO_BUTTON_NAME = "radioButton";

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_POOL_SIZE);
    private ObservableList<FileCipherJob> tableElementsList = FXCollections.observableArrayList();
    private Mode selectedBlockEncryptionMode = DEFAULT_BLOCK_ENCRYPTION_MODE;
    private Algorithm selectedEncryptionAlgorithm = DEFAULT_ENCRYPTION_ALGORITHM;
    private List<File> files;
    private boolean filesSelected = false;
    private int filesSelectedAmount = -1;
    private FileChooser fileChooser = new FileChooser();
    private Set<String> openedWindows = new HashSet<>();
    private LoginWindow loginWindow;
    private UserCredentials userCredentials;
    private final SimpleObjectProperty<SecretKey> privateKeyObservable = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<byte[]> initialVectorObservable = new SimpleObjectProperty<>();
    private Gson gson = new Gson();

    @FXML
    TableColumn<FileCipherJob, String> imageNameColumn;
    @FXML
    TableColumn<FileCipherJob, Double> progressColumn;
    @FXML
    TableColumn<FileCipherJob, String> statusColumn;
    @FXML
    TableView<FileCipherJob> filesTable;

    @FXML
    Pane mainPane;
    @FXML
    Label logLabel;
    @FXML
    Label labelInitialVector;
    @FXML
    Label labelPrivateKey;


    @FXML
    private void initialize() throws NoSuchAlgorithmException {
        initializeFileChooser();
        refreshTable();
        privateKeyObservable.addListener((observable, oldValue, newValue) ->
                labelPrivateKey.setText(DatatypeConverter.printHexBinary(newValue.getEncoded())));
        refreshPrivateKey();

        initialVectorObservable.addListener((observable, oldValue, newValue) ->
                labelInitialVector.setText(DatatypeConverter.printHexBinary(newValue)));
        refreshInitialVector();
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
        createFileJobsList(FileCipherJob.CipherMode.ENCRYPT);
    }

    @FXML
    void encryptFilesButtonClicked() {
        createFileJobsList(FileCipherJob.CipherMode.ENCRYPT);
        saveCipherSummary();
        if (filesSelected) {
            writeToLogLabel("Starting encryption of " + filesSelectedAmount + " files.");
            tableElementsList.forEach(task -> executor.submit(task));
        }
    }


    @FXML
    void decryptFilesButtonClicked() {
        createFileJobsList(FileCipherJob.CipherMode.DECRYPT);
        saveCipherSummary();
        if (filesSelected) {
            writeToLogLabel("Starting decryption of " + filesSelectedAmount + " files.");
            tableElementsList.forEach(task -> executor.submit(task));

        }
    }

    private void saveCipherSummary() {
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("privateKey", DatatypeConverter.printHexBinary(privateKeyObservable.get().getEncoded()));
        summaryMap.put("initialVector", DatatypeConverter.printHexBinary(initialVectorObservable.get()));
        String json = gson.toJson(summaryMap);

        File outputDetails = new File(System.getProperty("user.dir"), "outputSummary.json");
        try (PrintWriter writer = new PrintWriter(outputDetails)) {
            writer.write(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void refreshPrivateKey() throws NoSuchAlgorithmException {
        privateKeyObservable.setValue(KeyGenerator.getInstance(selectedEncryptionAlgorithm.name()).generateKey());
    }

    @FXML
    void refreshInitialVector() {
        if (selectedBlockEncryptionMode.initVectorRequired)
            initialVectorObservable.setValue(SecureRandom.getSeed(selectedEncryptionAlgorithm.initVectorSize));
        else
            initialVectorObservable.setValue("".getBytes());
    }

    private void createFileJobsList(FileCipherJob.CipherMode mode) {
        if (filesSelected) {
            tableElementsList.clear();
            files.forEach(file -> tableElementsList.add(
                    new FileCipherJob(file, mode, selectedEncryptionAlgorithm,
                            selectedBlockEncryptionMode, privateKeyObservable.get(), initialVectorObservable.get())));
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
        } else {
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
        RadioButton rb = (RadioButton) event.getSource();

        switch (getRadioButtonNumber(rb)) {
            case "1":
                selectedBlockEncryptionMode = Mode.ECB;
                break;
            case "2":
                selectedBlockEncryptionMode = Mode.CBC;
                break;
            case "3":
                selectedBlockEncryptionMode = Mode.CFB;
                break;
            case "4":
                selectedBlockEncryptionMode = Mode.OFB;
                break;
            default:
                throw new IllegalEventSourceException(rb.getId());
        }
        refreshInitialVector();
    }

    @FXML
    public void algorithmSelected(ActionEvent event) throws IllegalEventSourceException, NoSuchAlgorithmException {
        RadioButton rb = (RadioButton) event.getSource();

        switch (rb.getId()) {
            case "radioButtonAes":
                selectedEncryptionAlgorithm = Algorithm.AES;
                break;
            case "radioButtonDes":
                selectedEncryptionAlgorithm = Algorithm.DES;
                break;
            case "radioButtonBlowfish":
                selectedEncryptionAlgorithm = Algorithm.Blowfish;
                break;
            default:
                throw new IllegalEventSourceException(rb.getId());
        }
        refreshPrivateKey();
        refreshInitialVector();
        privateKeyObservable.setValue(KeyGenerator.getInstance(selectedEncryptionAlgorithm.name()).generateKey());
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