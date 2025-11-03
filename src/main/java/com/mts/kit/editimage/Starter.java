package com.mts.kit.editimage;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Starter implements Initializable {

    @FXML private ImageView imageStart;
    @FXML private AnchorPane root;
    @FXML private Button button;
    @FXML private StackPane stackPane;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private VBox brandingContainer;
    @FXML private Label sloganLabel;
    @FXML private Label copyrightLabel;
    private boolean eventEnabled = false;
    private boolean isDashboardLoaded = false;
    private static final Logger logger = Logger.getLogger(Starter.class.getName());
    private MyTaskUI myTaskUI;
    private MyLoadingScene myLoadingScene;
    private FadeTransition buttonFade ;

    @Override public void initialize(URL location, ResourceBundle resources) {
        myTaskUI = new MyTaskUI();
        myLoadingScene = new MyLoadingScene();
        buttonFade = new FadeTransition(Duration.seconds(1), button);
        setupButtonEffects();
        setupBrandingEffects();
        startView();
    }
    @FXML private void goToDashboardEnter(KeyEvent event) {
        if (!eventEnabled) return;
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
            loadDashboardWithProgress((Stage) ((Node) event.getSource()).getScene().getWindow());
            root.setOnKeyPressed(null);
            event.consume();
        }
    }
    @FXML private void goToDashboard(MouseEvent event) {
        if (eventEnabled) {
            loadDashboardWithProgress((Stage) ((Node) event.getSource()).getScene().getWindow());
            event.consume();
        }
    }


    private void setupButtonEffects() {
        button.setOnMouseEntered(e -> {
            button.setTextFill(Color.web("#ffffff"));

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            scaleUp.play();
            buttonFade.stop();
        });

        button.setOnMouseExited(e -> {
            button.setTextFill(Color.web("#ffffff3d"));

            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            if (buttonFade.getStatus() != Animation.Status.RUNNING)
                buttonFade.play();
        });
    }

    private void setupBrandingEffects() {
        sloganLabel.setOnMouseEntered(e -> {
            ScaleTransition sloganScale = new ScaleTransition(Duration.millis(300), sloganLabel);
            sloganScale.setToX(1.1);
            sloganScale.setToY(1.1);
            sloganScale.play();
        });

        sloganLabel.setOnMouseExited(e -> {
            ScaleTransition sloganScale = new ScaleTransition(Duration.millis(300), sloganLabel);
            sloganScale.setToX(1.0);
            sloganScale.setToY(1.0);
            sloganScale.play();
        });
    }

    private void startView() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> loadImageAsync());
        pause.play();
    }

    private void loadImageAsync() {
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() {
                return new Image(Objects.requireNonNull(getClass().getResource("/eMTSP.png")).toExternalForm());
            }
        };

        loadImageTask.setOnSucceeded(e ->
                Platform.runLater(() -> {
                    imageStart.setImage(loadImageTask.getValue());
                    playFadeAnimation();
                }));

        loadImageTask.setOnFailed(e ->
                Platform.runLater(() -> {
                    Throwable exception = loadImageTask.getException();
                    logger.log(Level.SEVERE, "Error loading startup image", exception);
                    showErrorMessage("خطأ في تحميل الصورة");
                    hideLoadingIndicator();
                    showBrandingWithAnimation();
                    showButtonWithAnimation();
                    showCopyrightWithAnimation();
                    eventEnabled = true;
                })
        );

        new Thread(loadImageTask).start();
    }

    private void playFadeAnimation() {
        hideLoadingIndicator();

        FadeTransition fade = new FadeTransition(Duration.seconds(2.0), imageStart);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setInterpolator(Interpolator.EASE_IN);
        fade.setOnFinished(event -> {
            showBrandingWithAnimation();
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                showButtonWithAnimation();
                showCopyrightWithAnimation();
                eventEnabled = true;
            });
            pause.play();
        });
        fade.play();
    }

    private void showBrandingWithAnimation() {
        brandingContainer.setVisible(true);
        brandingContainer.setOpacity(0.0);

        FadeTransition brandingFade = new FadeTransition(Duration.seconds(0.5), brandingContainer);
        brandingFade.setFromValue(0.0);
        brandingFade.setToValue(1.0);
        brandingFade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slideUp = new TranslateTransition(Duration.seconds(1.2), sloganLabel);
        slideUp.setFromY(140);
        slideUp.setToY(65);

        ScaleTransition sloganScale = new ScaleTransition(Duration.seconds(1), sloganLabel);
        sloganScale.setFromX(0.8);
        sloganScale.setFromY(0.8);
        sloganScale.setToX(1.0);
        sloganScale.setToY(1.0);

        brandingFade.play();
        slideUp.play();
        sloganScale.play();
    }

    private void showButtonWithAnimation() {
        button.setVisible(true);
        button.setOpacity(0.0);

        buttonFade.setFromValue(0.0);
        buttonFade.setToValue(1.0);
        buttonFade.setAutoReverse(true);
        buttonFade.setCycleCount(Animation.INDEFINITE);

        buttonFade.play();
    }

    private void showCopyrightWithAnimation() {
        copyrightLabel.setVisible(true);
        copyrightLabel.setOpacity(0.0);

        FadeTransition copyrightFade = new FadeTransition(Duration.seconds(0.5), copyrightLabel);
        copyrightFade.setFromValue(0);
        copyrightFade.setToValue(1.0);
        copyrightFade.play();
    }

    private void hideLoadingIndicator() {
        FadeTransition hideFade = new FadeTransition(Duration.seconds(0.3), loadingIndicator);
        hideFade.setFromValue(1.0);
        hideFade.setToValue(0.0);
        hideFade.setOnFinished(e -> loadingIndicator.setVisible(false));
        hideFade.play();
    }

    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setOpacity(0.0);

        FadeTransition errorFade = new FadeTransition(Duration.seconds(0.5), errorLabel);
        errorFade.setFromValue(0.0);
        errorFade.setToValue(1.0);
        errorFade.play();

        PauseTransition hideError = new PauseTransition(Duration.seconds(3));
        hideError.setOnFinished(e -> {
            FadeTransition hideErrorFade = new FadeTransition(Duration.seconds(0.5), errorLabel);
            hideErrorFade.setFromValue(1.0);
            hideErrorFade.setToValue(0.0);
            hideErrorFade.setOnFinished(ev -> errorLabel.setVisible(false));
            hideErrorFade.play();
        });
        hideError.play();
    }

    private void loadDashboardWithProgress(Stage stage) {
        if (isDashboardLoaded) return;
        isDashboardLoaded = true;

        FadeTransition exitFade = new FadeTransition(Duration.seconds(0.5), stackPane);
        exitFade.setFromValue(1.0);
        exitFade.setToValue(0.0);
        exitFade.setOnFinished(e -> {
            myTaskUI.progressDialog(stage, () -> Platform.runLater(this::loadDashboard), button);
            hideAllElements();
        });
        exitFade.play();

        eventEnabled = false;
    }
    // hide all elements
    private void hideAllElements() {
        stackPane.setVisible(false);
        imageStart.setVisible(false);
        button.setVisible(false);
        loadingIndicator.setVisible(false);
        errorLabel.setVisible(false);
        brandingContainer.setVisible(false);
        copyrightLabel.setVisible(false);
    }
    //loading Dashboard Page
    private void loadDashboard() {
        try {
            root.getChildren().clear();
            Parent parent = myLoadingScene.loadFXMLtoParent("hello-view.fxml");
            root.getChildren().add(parent);

            AnchorPane.setTopAnchor(parent, 0.0);
            AnchorPane.setBottomAnchor(parent, 0.0);
            AnchorPane.setLeftAnchor(parent, 0.0);
            AnchorPane.setRightAnchor(parent, 0.0);

            parent.setOpacity(0.0);
            FadeTransition dashboardFade = new FadeTransition(Duration.seconds(0.8), parent);
            dashboardFade.setFromValue(0.0);
            dashboardFade.setToValue(1.0);
            dashboardFade.play();

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error loading dashboard", ex);
            showErrorMessage("خطأ في تحميل لوحة التحكم");
            // reset starter page
            restoreStartupScreen();
        }
    }
    // reset starter page
    private void restoreStartupScreen() {
        isDashboardLoaded = false;
        stackPane.setVisible(true);
        imageStart.setVisible(true);
        button.setVisible(true);
        brandingContainer.setVisible(true);
        copyrightLabel.setVisible(true);
        eventEnabled = true;

        FadeTransition restoreFade = new FadeTransition(Duration.seconds(0.5), stackPane);
        restoreFade.setFromValue(0.0);
        restoreFade.setToValue(1.0);
        restoreFade.play();
    }
}

