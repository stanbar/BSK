package com.milbar.gui;


import com.stasbar.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Logger.info("Starting JavaFX main scene...");

        String fxmlFile = "/fxml/" + MainWindow.class.getSimpleName() + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(rootNode);
        stage.setTitle("Stanislaw Baranski 160518, Patryk Milewski 160503");
        stage.getIcons().add(new Image("images/logo.png"));
        stage.setScene(scene);
        stage.show();

        Logger.info("Scene started successfully !");
    }
}
