package com.mts.kit.editimage;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class MyAlert {

    public static void alertError(String headerText){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);
        alert.setContentText("اضغط OK للمتابعة");
        alert.getButtonTypes().setAll( ButtonType.OK);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNIFIED);

        alert.showAndWait();
    }

    public static void alertMessage(String headerText){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText("اضغط OK للمتابعة");
        alert.getButtonTypes().setAll(ButtonType.OK);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNIFIED);

        alert.showAndWait();
    }

    public static boolean alertAsk(String headerText){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(headerText);
        alert.setContentText("اضغط YES للمتابعة NO اللغاء");
        alert.getButtonTypes().setAll(ButtonType.NO,ButtonType.YES);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNIFIED);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    public static String alertSelection(String headerText , ButtonType... buttonTypes){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(headerText);
        alert.setContentText("اختار الزر الزي يتناسب مع اختيارك");
        alert.getButtonTypes().setAll(buttonTypes);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.initStyle(StageStyle.UNIFIED);

        Optional<ButtonType> result = alert.showAndWait();
        return result.map(ButtonType::getText).orElse(null);
    }
}
