package com.milbar.gui;


import com.stasbar.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Logger.info("Starting JavaFX main scene...");

        String fxmlFile = "/fxml/" + MainController.class.getSimpleName() + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(rootNode);

        stage.setTitle("Stanislaw Baranski 160518 und Patryk Milewski 160503");
        stage.setScene(scene);
        stage.show();

        Logger.info("Scene started successfully !");
    }
}
