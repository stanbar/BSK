package com.milbar.gui;

import com.milbar.logic.FileEncryptionJob;
import com.stasbar.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {

    private final static int THREADS_POOL_SIZE = 4;

    private ExecutorService executor = Executors.newFixedThreadPool(THREADS_POOL_SIZE);
    private ObservableList<FileEncryptionJob> tableElementsList = FXCollections.observableArrayList();
    private List<File> files;
    private boolean filesSelected = false;
    private int filesSelectedAmount = -1;
    private FileChooser fileChooser = new FileChooser();

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
    public void showAboutMenuBarClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/AboutWindowController.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToLogLabel(String log) {
        this.logLabel.setText(log);
        Logger.info(log);
    }
}
