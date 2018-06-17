package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.gui.configuration.ApplicationConfiguration;
import com.milbar.gui.helpers.LogLabel;
import com.milbar.gui.helpers.LoginController;
import com.milbar.gui.helpers.TogglesHelper;
import com.milbar.logic.abstracts.Mode;
import com.milbar.logic.exceptions.IllegalEventSourceException;
import com.milbar.logic.exceptions.InstanceInitializeException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.LoginManager;
import com.milbar.logic.login.wrappers.SessionToken;
import com.milbar.logic.security.jobs.AESFileCipherJob;
import com.milbar.logic.security.wrappers.FileWithMetadata;
import com.milbar.logic.security.wrappers.Password;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang.NotImplementedException;
import org.controlsfx.control.ListSelectionView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MainWindowController extends JavaFXController implements JavaFXWindowsListener {

    private final static Logger log = LoggerFactory.getLogger(MainWindowController.class);
    private LogLabel logLabel;
    
    private final static int THREADS_POOL_SIZE = 4;
    private final static Mode DEFAULT_BLOCK_ENCRYPTION_MODE = Mode.ECB;

    @FXML
    public ToggleGroup modeToggleGroup;
    @FXML
    public ArrayList<RadioButton> encryptionBlockTypeList;
    
    private Map<Mode, Toggle> modeToggleMap;
    private Map<Toggle, Mode> toggleModeMap;

    @FXML
    private ArrayList<RadioButton> encryptionModeList;
    
    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_POOL_SIZE);
    private ObservableList<AESFileCipherJob> tableElementsList = FXCollections.observableArrayList();
    private Mode selectedBlockEncryptionMode = DEFAULT_BLOCK_ENCRYPTION_MODE;
    private List<File> selectedFilesForEncryption = new ArrayList<>();
    private List<File> selectedFilesForDecryption = new ArrayList<>();
    private boolean filesSelected = false;
    private int notEncryptedFilesSelected = -1;
    private int encryptedFilesSelected = -1;
    private FileChooser notEncryptedFilesChooser = new FileChooser();
    private FileChooser encryptedFilesChooser = new FileChooser();
    private Set<String> openedWindows = new HashSet<>();
    private LoginController loginController = new LoginController();
    private LoginManager loginManager;

    @FXML
    private TableColumn<AESFileCipherJob, String> fileNameColumn;
    @FXML
    private TableColumn<AESFileCipherJob, Double> progressColumn;
    @FXML
    private TableColumn<AESFileCipherJob, String> statusColumn;
    @FXML
    private TableView<AESFileCipherJob> filesTable;

    @FXML
    private Label labelWithLogs;
    
    @FXML
    private ListSelectionView<String> selectedUsers;
    
    @FXML
    private void initialize() {
        
        logLabel = new LogLabel(labelWithLogs, log);
    
        try {
            loginManager = new LoginManager();
            refreshUsersList();
        } catch (InstanceInitializeException e) {
            logLabel.writeError("Failed to load available users list.");
        }
        
        modeToggleMap = TogglesHelper.prepareModeToggleMap(encryptionBlockTypeList);
        toggleModeMap = TogglesHelper.swapKeyValueMap(modeToggleMap);
        
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
        createFileJobsList();
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
        createFileJobsList();
    }

    private void selectToggleMode(Mode mode) {
        //modeToggleGroup.selectToggle(modeToggleMap.get(mode));
        modeToggleMap.get(mode).setSelected(true);
    }
    
    @FXML
    public void startButtonClicked() {
        createFileJobsList();
        tableElementsList.forEach(job -> executor.submit(job));
        Thread thread = new Thread(() -> {
            try {
                waitForJobToFinish();
                refreshJobsStatus();
            } catch (InterruptedException ignored) {
            
            }
        });
    }
    
    private void refreshJobsStatus() {
        // todo
    }
    
    private void waitForJobToFinish() throws InterruptedException {
        int counter = 0;
        while (counter != tableElementsList.size()) {
            for (AESFileCipherJob job : tableElementsList) {
                if (job.isFinished())
                    counter++;
            }
        }
        Thread.sleep(100);
    }
    
    private void createFileJobsList() {
        if (!loginController.isLoggedIn()) {
            logLabel.writeError("You are not logged in!");
            return;
        } else if (!filesSelected) {
            logLabel.writeError("You haven't chosen any files!");
            return;
        }
    
        tableElementsList.clear();
        Password password = new Password(loginController.getSessionToken().getSessionKey());
        Path currentUserPath = getCurrentUsersPath();
    
        selectedFilesForEncryption.forEach(file -> {
            tableElementsList.add(prepareEncryptionFileCipherJob(file, currentUserPath, password, selectedBlockEncryptionMode));
        });
    
        selectedFilesForDecryption.forEach(file -> {
            tableElementsList.add(prepareDecryptionFileCipherJob(file, currentUserPath, password, selectedBlockEncryptionMode));
        });

//            List<String> selectedUsersList = selectedUsers.getTargetItems();
//            selectedUsersList.forEach(selectedUser -> {
//                addJobToTable(sessionToken, selectedUser);
//            });
        
        refreshTable();
    }
    
    private AESFileCipherJob prepareEncryptionFileCipherJob(File file, Path currentUserPath, Password password, Mode mode) {
        FileWithMetadata fileWithMetadata = FileWithMetadata.getEncryptionInstance(file, currentUserPath, password, mode);
        return new AESFileCipherJob(fileWithMetadata);
    }
    
    private AESFileCipherJob prepareDecryptionFileCipherJob(File file, Path currentUserPath, Password password, Mode mode) {
        try {
            FileWithMetadata fileWithMetadata = FileWithMetadata.getDecryptionInstance(file, currentUserPath, password, mode);
            return new AESFileCipherJob(fileWithMetadata);
        } catch (IOException e) {
            logLabel.writeError("Failed to read file header " + file.getName());
            return AESFileCipherJob.getFailedInstance();
        }
    }
    
    private Path getCurrentUsersPath() {
        String username = loginController.getUserCredentials().getUsername();
        return ApplicationConfiguration.getSingleUserDataPath(username);
    }
    
    private void refreshTable() {

        fileNameColumn.setCellValueFactory( // file name
                cell -> new SimpleStringProperty(cell.getValue().getFile().getName()));

        statusColumn.setCellValueFactory( // status information
                cell -> cell.getValue().getStatusProperty());

        progressColumn.setCellFactory( // javafx add progress bar
                ProgressBarTableCell.forTableColumn());

        progressColumn.setCellValueFactory( // linking progress bar
                cell -> cell.getValue().getProgressProperty().asObject());

        
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
            logLabel.writeInfo("Selected block mode type: " + selectedBlockEncryptionMode.fullName);
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
    
        refreshUsersList();
    }

    @Override
    public void closeWindow() {
    
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        throw new NotImplementedException("This controller can't have parent controllers.");
    }
    
    private void refreshUsersList() {
        List<String> usersList = loginManager.getUsersList();
        selectedUsers.getSourceItems().clear();
        selectedUsers.getTargetItems().clear();
        selectedUsers.getSourceItems().addAll(usersList);
    }
    
    public void loginUser(SessionToken sessionToken) {
        loginController.login(sessionToken);
        logLabel.writeInfo("New session token valid until: " + sessionToken.getSessionValidUntil().toString());
        Thread thread = new Thread(sessionToken);
        thread.start();
    }
    
    @FXML
    public void clearListButtonClicked() {
        tableElementsList.clear();
        selectedFilesForEncryption.clear();
        selectedFilesForDecryption.clear();
    }
}