package com.mts.kit.editimage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;

public class MyLoadingScene {

    public void loadingWindow(String Url , Scene scene){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Url));
            Parent parent =loader.load();
            scene.setRoot(parent);
        }
        catch (Exception e){
            MyAlert.alertError("خطاء في تحميل النافذة");
        }
    }

    public FXMLLoader loadingFXML(String Url){
        try {
            return new FXMLLoader(getClass().getResource(Url)).load();
        }
        catch (Exception e){
            MyAlert.alertError("خطاء في تحميل النافذة");
            return null ;
        }
    }

    public Parent loadFXMLtoParent(String Url){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Url));
            return loader.load();
        }
        catch (Exception e){
            MyAlert.alertError("خطاء في تحميل النافذة");
            throw new RuntimeException(e);
        }
    }

    public void secondaryStage(String fxml, String title, double width , double height){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);

            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMaxWidth(width);
            stage.setMaxHeight(height);
            stage.setMinWidth(width);
            stage.setMinHeight(height);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            MyAlert.alertError("خطاء في فتح النافذة");
            throw new RuntimeException(e);
        }
    }



}
