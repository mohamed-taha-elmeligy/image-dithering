module com.mts.kit.editimage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires javafx.graphics;
    requires javafx.swing;
    requires java.logging;

    opens com.mts.kit.editimage to javafx.fxml;
    exports com.mts.kit.editimage;
}