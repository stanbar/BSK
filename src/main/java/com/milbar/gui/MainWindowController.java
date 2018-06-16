package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.gui.helpers.LogLabel;
import com.milbar.gui.helpers.LoginController;
import com.milbar.gui.helpers.TogglesHelper;
import com.milbar.logic.abstracts.Algorithm;
import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.exceptions.IllegalEventSourceException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.security.jobs.AESFileCipherJob;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang.NotImplementedException;

import javax.crypto.KeyGenerator;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private ObservableList<AESFileCipherJob> tableElementsList = FXCollections.observableArrayList();
    private Mode selectedBlockEncryptionMode = DEFAULT_BLOCK_ENCRYPTION_MODE;
    private Algorithm selectedEncryptionAlgorithm = DEFAULT_ENCRYPTION_ALGORITHM;
    private List<File> selectedFilesForEncryption;
    private List<File> selectedFilesForDecryption;
    private boolean filesSelected = false;
    private int notEncryptedFilesSelected = -1;
    private int encryptedFilesSelected = -1;
    private FileChooser notEncryptedFilesChooser = new FileChooser();
    private FileChooser encryptedFilesChooser = new FileChooser();
    private Set<String> openedWindows = new HashSet<>();
    private SessionToken sessionToken;
    private final SimpleObjectProperty<Key> privateKeyObservable = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<byte[]> initialVectorObservable = new SimpleObjectProperty<>();
    private LoginController loginController = new LoginController();

    @FXML
    private TableColumn<AESFileCipherJob, String> fileNameColumn;
    @FXML
    private TableColumn<AESFileCipherJob, Boolean> isEncryptedColumn;
    @FXML
    private TableColumn<AESFileCipherJob, String> newFileName;
    @FXML
    private TableColumn<AESFileCipherJob, Double> progressColumn;
    @FXML
    private TableColumn<AESFileCipherJob, String> statusColumn;
    @FXML
    private TableView<AESFileCipherJob> filesTable;

    @FXML
    private Label labelWithLogs;
    
    
    @FXML
    private void initialize() {
        logLabel = new LogLabel(labelWithLogs, log);
    
        modeToggleMap = TogglesHelper.prepareModeToggleMap(encryptionBlockTypeList);
        toggleModeMap = TogglesHelper.swapKeyValueMap(modeToggleMap);
        
        algorithmToggleMap = TogglesHelper.prepareAlgorithmToggleMap(encryptionModeList);
        toggleAlgorithmMap = TogglesHelper.swapKeyValueMap(algorithmToggleMap);
        isEncryptedColumn.setEditable(true);
        newFileName.setEditable(true);
        filesTable.setEditable(true);
        
        initializeFileChooser();
        refreshTable();
        
    }

    private void initializeFileChooser() {
        notEncryptedFilesChooser.setTitle("Select files to encrypt.");
        encryptedFilesChooser.setTitle("Select files to decrypt.");
        //notEncryptedFilesChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG images", "*.jpg"));
    }

    @FXML
    void menuBarActionCloseApplication() {
    
    }

    @FXML
    void chooseFilesForEncryptionButtonClicked() {
        selectedFilesForEncryption = notEncryptedFilesChooser.showOpenMultipleDialog(null);
        if (selectedFilesForEncryption == null) {
            filesSelected = false;
            return;
        }

        encryptedFilesSelected = selectedFilesForEncryption.size();
        filesSelected = encryptedFilesSelected > 0;
        logLabel.writeInfo("Selected " + encryptedFilesSelected + " files for encryption.");
        createFileJobsList(AESFileCipherJob.CipherMode.ENCRYPT);
    }
    
    @FXML
    void chooseFilesForDecryptionButtonClicked() {
        selectedFilesForDecryption = encryptedFilesChooser.showOpenMultipleDialog(null);
        if (selectedFilesForDecryption == null) {
            filesSelected = false;
            return;
        }
    
        notEncryptedFilesSelected = selectedFilesForDecryption.size();
        filesSelected = encryptedFilesSelected > 0;
        logLabel.writeInfo("Selected " + encryptedFilesSelected + " files for decryption.");
        createFileJobsList(AESFileCipherJob.CipherMode.ENCRYPT);
    }

    @FXML
    void encryptFilesButtonClicked() {
        createFileJobsList(AESFileCipherJob.CipherMode.ENCRYPT);
        if (filesSelected) {
            logLabel.writeInfo("Starting encryption of " + encryptedFilesSelected + " files.");
            tableElementsList.forEach(task -> executor.submit(task));
        }
    }

    @FXML
    void decryptFilesButtonClicked() {
        createFileJobsList(AESFileCipherJob.CipherMode.DECRYPT);
        if (filesSelected) {
            logLabel.writeInfo("Starting decryption of " + encryptedFilesSelected + " files.");
            tableElementsList.forEach(task -> executor.submit(task));

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

    private void createFileJobsList(Mode blockMode) {
        if (filesSelected) {
            tableElementsList.clear();
            selectedFilesForEncryption.forEach(file -> tableElementsList.add(prepareFileCipherJob(file, )));
            
        }
        refreshTable();
    }

    private AESFileCipherJob prepareFileCipherJob(File inputFile) {
    
    }
    
    private void refreshTable() {

        fileNameColumn.setCellValueFactory( // file name
                cell -> new SimpleStringProperty(cell.getValue().getFile().getName()));

        isEncryptedColumn.setCellFactory( // is encrypted checkbox
                cell -> new CheckBoxTableCell<>());
    
        newFileName.setCellFactory( // new file name (optional)
                cell -> new TextFieldTableCell<>());
        
        statusColumn.setCellValueFactory( // status information
                cell -> cell.getValue().getStatusProperty());

        progressColumn.setCellFactory( // javafx add progress bar
                ProgressBarTableCell.forTableColumn());

        progressColumn.setCellValueFactory( // linking progress bar
                cell -> cell.getValue().getProgressProperty().asObject());

        
        filesTable.setItems(tableElementsList);
        filesTable.setEditable(true);
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
        throw new NotImplementedException("This controller can't have parent controllers.");
    }
    
    public void loginUser(SessionToken sessionToken) {
        loginController.login(sessionToken);
        logLabel.writeInfo("New session token valid until: " + sessionToken.getSessionValidUntil().toString());
    }
}