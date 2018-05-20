package com.milbar.gui;

import com.milbar.ConfigManager;
import com.milbar.Utils;
import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.FileCipherJob;
import com.milbar.logic.encryption.Algorithm;
import com.milbar.logic.encryption.Mode;
import com.milbar.logic.exceptions.IllegalEventSourceException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.wrappers.UserCredentials;
import com.milbar.model.CipherConfig;
import javafx.application.Platform;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang.NotImplementedException;

import javax.crypto.KeyGenerator;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController extends JavaFXController implements JavaFXWindowsListener {

    private final static Logger logger = LoggerFactory.getLogger(MainWindowController.class);
    
    private final static int THREADS_POOL_SIZE = 4;
    private final static Mode DEFAULT_BLOCK_ENCRYPTION_MODE = Mode.ECB;
    private final static Algorithm DEFAULT_ENCRYPTION_ALGORITHM = Algorithm.DES;

    @FXML
    public ToggleGroup modeToggleGroup;
    @FXML
    public RadioButton radioButtonECB;
    @FXML
    public RadioButton radioButtonCBC;
    @FXML
    public RadioButton radioButtonCFB;
    @FXML
    public RadioButton radioButtonOFB;
    private Map<Mode, RadioButton> modeToggleMap = new HashMap<Mode, RadioButton>() {{
        Platform.runLater(() -> {
            put(Mode.ECB, radioButtonECB);
            put(Mode.CBC, radioButtonCBC);
            put(Mode.CFB, radioButtonCFB);
            put(Mode.OFB, radioButtonOFB);
        });
    }};


    @FXML
    public ToggleGroup algorithmToggleGroup;
    @FXML
    public RadioButton radioButtonDES;
    @FXML
    public RadioButton radioButtonDESeee;
    @FXML
    public RadioButton radioButtonDESede2;
    @FXML
    public RadioButton radioButtonDESede3;
    @FXML
    public RadioButton radioButtonAES;
    @FXML
    public RadioButton radioButtonBlowfish;
    private Map<Algorithm, Toggle> algorithmToggleMap = new HashMap<Algorithm, Toggle>() {{
        Platform.runLater(() -> {
            put(Algorithm.AES, radioButtonAES);
            put(Algorithm.DES, radioButtonDES);
            put(Algorithm.DESeee, radioButtonDESeee);
            put(Algorithm.DESede2, radioButtonDESede2);
            put(Algorithm.DESede3, radioButtonDESede3);
            put(Algorithm.Blowfish, radioButtonBlowfish);
        });
    }};


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
    private final SimpleObjectProperty<Key> privateKeyObservable = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<byte[]> initialVectorObservable = new SimpleObjectProperty<>();


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
                labelPrivateKey.setText(Utils.byteArrayToHex(newValue.getEncoded())));
        refreshPrivateKey();

        initialVectorObservable.addListener((observable, oldValue, newValue) ->
                labelInitialVector.setText(Utils.byteArrayToHex(newValue)));
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
        if (filesSelected) {
            writeToLogLabel("Starting encryption of " + filesSelectedAmount + " files.");
            tableElementsList.forEach(task -> executor.submit(task));
        }
    }


    @FXML
    void decryptFilesButtonClicked() {
        createFileJobsList(FileCipherJob.CipherMode.DECRYPT);
        if (filesSelected) {
            writeToLogLabel("Starting decryption of " + filesSelectedAmount + " files.");
            tableElementsList.forEach(task -> executor.submit(task));

        }
    }

    @FXML
    void saveCipherConfig() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save cipher config");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {

            file = ConfigManager.saveConfig(file, new CipherConfig(privateKeyObservable.get().getEncoded(),
                    initialVectorObservable.get(),
                    selectedEncryptionAlgorithm,
                    selectedBlockEncryptionMode));
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, e.getMessage());
            }
        }


    }

    @FXML
    void loadCipherConfig() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load cipher config");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json"),
                new FileChooser.ExtensionFilter("All types", "*.*")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            CipherConfig config = ConfigManager.loadConfig(file);
            privateKeyObservable.setValue(config.getSecretKey());
            initialVectorObservable.setValue(config.getInitialVectorBytes());
            selectToggleMode(config.getMode());
            selectToggleAlgorithm(config.getAlgorithm());
        }
    }

    private void selectToggleMode(Mode mode) {
        //modeToggleGroup.selectToggle(modeToggleMap.get(mode));
        modeToggleMap.get(mode).setSelected(true);
    }

    private void selectToggleAlgorithm(Algorithm algorithm) {
        //algorithmToggleGroup.selectToggle(algorithmToggleMap.get(algorithm));
        algorithmToggleMap.get(algorithm).setSelected(true);
    }

    private void refreshPrivateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(selectedEncryptionAlgorithm.algorithmName);
        keyGenerator.init(selectedEncryptionAlgorithm.keySize);

        privateKeyObservable.setValue(keyGenerator.generateKey());
    }

    private void refreshInitialVector() {
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

        switch (rb.getId()) {
            case "radioButtonECB":
                selectedBlockEncryptionMode = Mode.ECB;
                break;
            case "radioButtonCBC":
                selectedBlockEncryptionMode = Mode.CBC;
                break;
            case "radioButtonCFB":
                selectedBlockEncryptionMode = Mode.CFB;
                break;
            case "radioButtonOFB":
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
            case "radioButtonAES":
                selectedEncryptionAlgorithm = Algorithm.AES;
                break;
            case "radioButtonDES":
                selectedEncryptionAlgorithm = Algorithm.DES;
                break;
            case "radioButtonDESeee":
                selectedEncryptionAlgorithm = Algorithm.DESeee;
                break;
            case "radioButtonDESede2":
                selectedEncryptionAlgorithm = Algorithm.DESede2;
                break;
            case "radioButtonDESede3":
                selectedEncryptionAlgorithm = Algorithm.DESede3;
                break;
            case "radioButtonBlowfish":
                selectedEncryptionAlgorithm = Algorithm.Blowfish;
                break;
            default:
                throw new IllegalEventSourceException(rb.getId());
        }
        refreshPrivateKey();
        refreshInitialVector();
    }


    @Override
    public void windowClosed(String callerClassName) {
        if (!openedWindows.remove(callerClassName))
            throw new UnexpectedWindowEventCall("Class name: " + callerClassName);
    }

    private void writeToLogLabel(String singleLog) {
        this.logLabel.setText(singleLog);
        logger.log(Level.INFO, singleLog);
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
    
    @Override
    public void closeWindow() {
    
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        throw new NotImplementedException("This controlled can't have parent controllers.");
    }

}