package com.milbar;


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
        Logger.info("Starting JavaFX scene...");

        String fxmlFile = "/com/milbar/" + MainController.class.getSimpleName() + ".fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(rootNode, 400, 200);

        stage.setTitle("Patryk Milewski und Stanislaw Baranski");
        stage.setScene(scene);
        stage.show();

        Logger.info("Scene started successfully !");

    }
}
