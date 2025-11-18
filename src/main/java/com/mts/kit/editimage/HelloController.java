package com.mts.kit.editimage;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HelloController {
    @FXML private ImageView originalImage;
    @FXML private ImageView processedImage;
    @FXML private ComboBox<String> methodCombo;
    @FXML private Slider levelSlider;
    @FXML private Label levelLabel;
    @FXML private Button exportBtn;
    @FXML private Button loadBtn;
    @FXML private Button resetBtn;
    @FXML private VBox originalPlaceholder;
    @FXML private VBox processedPlaceholder;

    private BufferedImage currentImage;
    private BufferedImage processedBufferedImage;
    private boolean isProcessing = false;

    @FXML
    public void initialize() {
        methodCombo.setValue("Ordered Dithering");

        levelSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            levelLabel.setText(String.valueOf(newVal.intValue()));
        });

        exportBtn.setDisable(true);
        resetBtn.setDisable(true);
    }

    @FXML
    private void loadImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("اختر صورة - Select Image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("صور - Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("جميع الملفات - All Files", "*.*")
        );

        File file = fc.showOpenDialog(originalImage.getScene().getWindow());

        if (file != null) {
            try {
                currentImage = ImageIO.read(file);

                if (currentImage == null) {
                    showAlert("خطأ - Error", "تنسيق الصورة غير مدعوم\nUnsupported image format", Alert.AlertType.ERROR);
                    return;
                }

                Image img = SwingFXUtils.toFXImage(currentImage, null);
                originalImage.setImage(img);

                originalPlaceholder.setVisible(false);
                originalPlaceholder.setManaged(false);

                processedImage.setImage(null);
                processedBufferedImage = null;

                processedPlaceholder.setVisible(true);
                processedPlaceholder.setManaged(true);

                exportBtn.setDisable(true);
                resetBtn.setDisable(false);

                showInfo("تم التحميل", "تم تحميل الصورة بنجاح\nأبعاد الصورة: " + currentImage.getWidth() + "x" + currentImage.getHeight());

            } catch (IOException e) {
                showAlert("خطأ - Error", "فشل تحميل الصورة\nFailed to load image\n" + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void applyDithering() {
        if (currentImage == null) {
            showAlert("تحذير - Warning", "يجب تحميل صورة أولاً\nPlease load an image first", Alert.AlertType.WARNING);
            return;
        }

        if (isProcessing) {
            showAlert("تحذير - Warning", "جاري معالجة الصورة...\nProcessing in progress...", Alert.AlertType.WARNING);
            return;
        }

        int levels = (int) levelSlider.getValue();
        String method = methodCombo.getValue();

        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("معالجة - Processing");
        progressAlert.setHeaderText("جاري معالجة الصورة...\nProcessing image...");
        progressAlert.setContentText("الطريقة: " + method + "\nالمستوى: " + levels);

        Task<BufferedImage> task = new Task<BufferedImage>() {
            @Override
            protected BufferedImage call() throws Exception {
                isProcessing = true;
                BufferedImage result;

                switch (method) {
                    case "Ordered Dithering":
                        result = orderedDithering(currentImage, levels);
                        break;
                    case "Random Dithering":
                        result = randomDithering(currentImage, levels);
                        break;
                    case "Error Diffusion":
                        result = errorDiffusion(currentImage, levels);
                        break;
                    default:
                        result = orderedDithering(currentImage, levels);
                }

                return result;
            }
        };

        task.setOnSucceeded(e -> {
            BufferedImage result = task.getValue();
            processedBufferedImage = result;
            processedImage.setImage(SwingFXUtils.toFXImage(result, null));

            processedPlaceholder.setVisible(false);
            processedPlaceholder.setManaged(false);

            exportBtn.setDisable(false);
            isProcessing = false;
            progressAlert.close();
            showInfo("تم بنجاح", "تمت معالجة الصورة بنجاح\nImage processed successfully");
        });

        task.setOnFailed(e -> {
            isProcessing = false;
            progressAlert.close();
            showAlert("خطأ - Error", "فشلت معالجة الصورة\nProcessing failed", Alert.AlertType.ERROR);
            task.getException().printStackTrace();
        });

        new Thread(task).start();
        progressAlert.show();
    }

    private BufferedImage orderedDithering(BufferedImage img, int levels) {

        int[][] bayerMatrix = {
                {0, 8, 2, 10},
                {12, 4, 14, 6},
                {3, 11, 1, 9},
                {15, 7, 13, 5}
        };

        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        int step = 255 / (levels - 1);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);

                int threshold = (bayerMatrix[y % 4][x % 4] * 16);
                int newGray = ((gray + threshold) / step) * step;
                newGray = Math.min(255, Math.max(0, newGray));

                int newRgb = (newGray << 16) | (newGray << 8) | newGray;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    private BufferedImage randomDithering(BufferedImage img, int levels) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        int step = 255 / (levels - 1);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);

                // إضافة ضوضاء عشوائية
                int noise = (int)(Math.random() * 64) - 32;
                int newGray = ((gray + noise) / step) * step;
                newGray = Math.min(255, Math.max(0, newGray));

                int newRgb = (newGray << 16) | (newGray << 8) | newGray;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    private BufferedImage errorDiffusion(BufferedImage img, int levels) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        int step = 255 / (levels - 1);
        int[][] errors = new int[img.getHeight()][img.getWidth()];

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b) + errors[y][x];
                gray = Math.min(255, Math.max(0, gray));

                int newGray = Math.round((float)gray / step) * step;
                newGray = Math.min(255, Math.max(0, newGray));
                int error = gray - newGray;

                if (x + 1 < img.getWidth())
                    errors[y][x + 1] += error * 7 / 16;

                if (y + 1 < img.getHeight()) {
                    if (x > 0)
                        errors[y + 1][x - 1] += error * 3 / 16;

                    errors[y + 1][x] += error * 5 / 16;

                    if (x + 1 < img.getWidth())
                        errors[y + 1][x + 1] += error * 1 / 16;
                }

                int newRgb = (newGray << 16) | (newGray << 8) | newGray;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    @FXML
    private void exportImage() {
        if (processedBufferedImage == null) {
            showAlert("تحذير - Warning", "لا توجد صورة للتصدير\nNo image to export", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("حفظ الصورة - Save Image");
        fc.setInitialFileName("dithered_image.png");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Image", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Image", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP Image", "*.bmp")
        );

        File file = fc.showSaveDialog(processedImage.getScene().getWindow());

        if (file != null) {
            try {
                String fileName = file.getName().toLowerCase();
                String format = "png";

                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    format = "jpg";
                } else if (fileName.endsWith(".bmp")) {
                    format = "bmp";
                }

                ImageIO.write(processedBufferedImage, format, file);
                showAlert("نجح - Success",
                        "تم حفظ الصورة بنجاح\nImage saved successfully\n\nالمسار: " + file.getAbsolutePath(),
                        Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                showAlert("خطأ - Error",
                        "فشل حفظ الصورة\nFailed to save image\n" + e.getMessage(),
                        Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void resetImage() {
        processedImage.setImage(null);
        processedBufferedImage = null;
        exportBtn.setDisable(true);

        processedPlaceholder.setVisible(true);
        processedPlaceholder.setManaged(true);

        if (currentImage == null) {
            resetBtn.setDisable(true);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> alert.close());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}