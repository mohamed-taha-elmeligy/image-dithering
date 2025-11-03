package com.mts.kit.editimage;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyTaskUI {

    private Button button ;
    private Stage progressStage ;
    private Task<Void> task;
    private final static ExecutorService service = Executors.newFixedThreadPool(1);

    /**
     * Displays a modal progress dialog with an indeterminate progress indicator.
     * This dialog is transparent, non-resizable, and centered on the screen.
     *
     * @param ownerStage The primary stage that owns this progress dialog.
     *                   It ensures the dialog is modal relative to the owner.
     * @param taskRunnable The task to execute in the background.
     * @param startButton  The button to disable during execution.
     * @throws IllegalStateException If the secondaryStage is not properly initialized.
     */
    public void progressDialog(Stage ownerStage , Runnable taskRunnable , Button startButton){

        if (progressStage != null && progressStage.isShowing()) {
            return;
        }
        button = startButton;

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(ProgressIndicator.USE_PREF_SIZE);
        progressIndicator.setStyle("-fx-background-color: transparent;");
        progressIndicator.setMouseTransparent(true);

        Scene scene = new Scene(progressIndicator, 150, 150);
        scene.setFill(Color.TRANSPARENT);

        progressStage = new Stage();
        progressStage.initOwner(ownerStage);
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setResizable(false);
        progressStage.initStyle(StageStyle.TRANSPARENT);
        progressStage.centerOnScreen();
        progressStage.setScene(scene);

         task = new Task<>() {
             @Override protected Void call() {
                 try {
                     taskRunnable.run();
                 } catch (Exception ex) {
                     throw new RuntimeException(ex);
                 }
                 return null;
             }
             @Override
             protected void running (){
                 Platform.runLater(()->{
                 if (startButton != null)
                     startButton.setDisable(true);});
             }
             @Override
             protected void succeeded(){
                Platform.runLater(()-> endProgressDialog());
             }
             @Override
             protected void failed (){
                Platform.runLater(()-> {
                    MyAlert.alertError("❌ فشل التحميل");
                    endProgressDialog();
                });
             }
        };
        start();
    }

    private void start (){
        service.submit(task);
        progressStage.show();
    }
    private void endProgressDialog(){
        Platform.runLater(()->{
            progressStage.close();
            if (button != null) button.setDisable(false);
        });
    }
    public static void shutDown() {
        service.shutdown();
    }

    public static void shutDownAll() {
        service.shutdownNow();
    }

}

