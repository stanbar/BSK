package com.milbar.gui;

import com.milbar.ConfigManager;
import com.milbar.Utils;
import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.gui.helpers.LogLabel;
import com.milbar.gui.helpers.LoginController;
import com.milbar.gui.helpers.TogglesHelper;
import com.milbar.logic.FileCipherJob;
import com.milbar.logic.encryption.Algorithm;
import com.milbar.logic.encryption.Mode;
import com.milbar.logic.exceptions.IllegalEventSourceException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.model.CipherConfig;
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

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private LogLabel logLabel;
    
    private final static int THREADS_POOL_SIZE = 4;
    private final static Mode DEFAULT_BLOCK_ENCRYPTION_MODE = Mode.ECB;
    private final static Algorithm DEFAULT_ENCRYPTION_ALGORITHM = Algorithm.DES;

    @FXML
    public ToggleGroup modeToggleGroup;
    @FXML
    public ArrayList<RadioButton> encryptionBlockTypeList;
    
    private Map<Mode, Toggle> modeToggleMap;
    private Map<Toggle, Mode> toggleModeMap;

    @FXML
    public ToggleGroup algorithmToggleGroup;
    @FXML
    private ArrayList<RadioButton> encryptionModeList;
    
    private Map<Algorithm, Toggle> algorithmToggleMap;
    private Map<Toggle, Algorithm> toggleAlgorithmMap;
    

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_POOL_SIZE);
    private ObservableList<FileCipherJob> tableElementsList = FXCollections.observableArrayList();
    private Mode selectedBlockEncryptionMode = DEFAULT_BLOCK_ENCRYPTION_MODE;
    private Algorithm selectedEncryptionAlgorithm = DEFAULT_ENCRYPTION_ALGORITHM;
    private List<File> files;
    private boolean filesSelected = false;
    private int filesSelectedAmount = -1;
    private FileChooser fileChooser = new FileChooser();
    private Set<String> openedWindows = new HashSet<>();
    private SessionToken sessionToken;
    private final SimpleObjectProperty<Key> privateKeyObservable = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<byte[]> initialVectorObservable = new SimpleObjectProperty<>();
    private LoginController loginController = new LoginController();

    @FXML
    private TableColumn<FileCipherJob, String> imageNameColumn;
    @FXML
    private TableColumn<FileCipherJob, Double> progressColumn;
    @FXML
    private TableColumn<FileCipherJob, String> statusColumn;
    @FXML
    private TableView<FileCipherJob> filesTable;

    @FXML
    private Label labelWithLogs;
    @FXML
    private Label labelInitialVector;
    @FXML
    private Label labelPrivateKey;
    
    
    @FXML
    private void initialize() throws NoSuchAlgorithmException {
        logLabel = new LogLabel(labelWithLogs, log);
    
        modeToggleMap = TogglesHelper.prepareModeToggleMap(encryptionBlockTypeList);
        toggleModeMap = TogglesHelper.swapKeyValueMap(modeToggleMap);
        
        algorithmToggleMap = TogglesHelper.prepareAlgorithmToggleMap(encryptionModeList);
        toggleAlgorithmMap = TogglesHelper.swapKeyValueMap(algorithmToggleMap);
        
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
        logLabel.writeInfo("Selected " + filesSelectedAmount + " files for encryption.");
        createFileJobsList(FileCipherJob.CipherMode.ENCRYPT);
    }

    @FXML
    void encryptFilesButtonClicked() {
        createFileJobsList(FileCipherJob.CipherMode.ENCRYPT);
        if (filesSelected) {
            logLabel.writeInfo("Starting encryption of " + filesSelectedAmount + " files.");
            tableElementsList.forEach(task -> executor.submit(task));
        }
    }

    @FXML
    void decryptFilesButtonClicked() {
        createFileJobsList(FileCipherJob.CipherMode.DECRYPT);
        if (filesSelected) {
            logLabel.writeInfo("Starting decryption of " + filesSelectedAmount + " files.");
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
                log.log(Level.SEVERE, e.getMessage());
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
        if (openedWindows.add(LoginWindow.class.getCanonicalName())) {
            try {
                LoginWindow loginWindow = new LoginWindow(this);
            } catch (IOException e) {
                e.printStackTrace();
                logLabel.writeError("Failed to open login window.");
            }
        } else {
            logLabel.writeWarning("Login window is already opened!");
        }
    }

    @FXML
    public void logoutMenuBarClicked() {
        logoutCurrentUser();
    }

    private void logoutCurrentUser() {
        if (loginController.isLoggedIn()) {
            loginController.logout();
            logLabel.writeInfo("Successfully logged out.");
        }
        else
            logLabel.writeError("You are not logged in.");
    }

    @FXML
    public void showAboutMenuBarClicked() {
        openNewWindow("fxml/AboutWindow.fxml", "About");
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
            logLabel.writeError("Failed to open a new window " + windowTitle);
            e.printStackTrace();
        }
    }
    
    @FXML
    public void blockModeSelected(ActionEvent event) throws IllegalEventSourceException {
        Toggle rb = (Toggle) event.getSource();

        if (toggleModeMap.containsKey(rb)) {
            selectedBlockEncryptionMode = toggleModeMap.get(rb);
            refreshInitialVector();
            logLabel.writeInfo("Selected block mode type: " + selectedBlockEncryptionMode.fullName);
        }
        else {
            logLabel.writeError("Illegal toggle selected: " + event.getSource().toString());
            throw new IllegalEventSourceException(event.getSource().toString());
        }
    }

    @FXML
    public void algorithmSelected(ActionEvent event) throws IllegalEventSourceException, NoSuchAlgorithmException {
        Toggle rb = (Toggle) event.getSource();

        if (toggleAlgorithmMap.containsKey(rb)) {
            selectedEncryptionAlgorithm = toggleAlgorithmMap.get(rb);
            refreshPrivateKey();
            refreshInitialVector();
            logLabel.writeInfo("Selected encryption algorithm type: " + selectedEncryptionAlgorithm.algorithmName);
        }
        else {
            logLabel.writeError("Illegal toggle selected: " + event.getSource().toString());
            throw new IllegalEventSourceException(event.getSource().toString());
        }
    }

    @Override
    public void windowClosed(String callerClassName) {
        if (!openedWindows.remove(callerClassName))
            throw new UnexpectedWindowEventCall("Class name: " + callerClassName);
    }

    @Override
    public void closeWindow() {
    
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        throw new NotImplementedException("This controlled can't have parent controllers.");
    }
    
    public void setSessionToken(SessionToken sessionToken) {
        this.sessionToken = sessionToken;
        logLabel.writeInfo("New session token, username: " + sessionToken.getUsername()
                        + ", valid until: " + sessionToken.getValidDate().toString());
    }
    
    public void loginUser(SessionToken sessionToken) {
        loginController.login(sessionToken);
        logLabel.writeInfo("New session token, username: " + sessionToken.getUsername()
                + ", valid until: " + sessionToken.getValidDate().toString());
    }
}