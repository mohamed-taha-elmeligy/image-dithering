package com.mts.kit.editimage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("starter.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),800,600);

        stage.getIcons().add(new Image("Screenshot 2025-11-02 232950.png"));
        stage.setMaximized(true);
        stage.setTitle("Image Dithering");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}