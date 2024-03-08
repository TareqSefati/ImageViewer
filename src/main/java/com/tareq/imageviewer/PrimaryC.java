package com.tareq.imageviewer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.tareq.imageviewer.services.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrimaryC implements Initializable {

    private File file = null;
    private Image selectedImage;
    private int index;
    private List<String> imageFilesName;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem open;
    @FXML
    private MenuItem close;
    @FXML
    private MenuItem colorChannels;
    @FXML
    private MenuItem about;
    @FXML
    private ImageView imageView;
    @FXML
    private Label fileLabel;
    @FXML
    private Button btn_previous;
    @FXML
    private JFXButton btnImageDetails;
    @FXML
    private JFXToggleButton btnGreyScaleToggle;
    @FXML
    private Button btn_next;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label labelGrayImageLoading;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setVisible(false);
        progressIndicator.setVisible(false);
        labelGrayImageLoading.setVisible(false);
        this.imageFilesName = new ArrayList<>();
    }

    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        ExtensionFilter fileExtensions = new ExtensionFilter("Image File", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(fileExtensions);
        //fileChooser.showOpenDialog((MenuItem)event.getTarget()).getParentPopup().getScene().getWindow(); // works good
        file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (file != null) {
            File parentDir = file.getParentFile();
            //System.out.println("file name: "+ parentDir.listFiles()[0].getAbsolutePath());
            int count = 0;
            imageFilesName.clear();
            for (int i = 0; i < parentDir.list().length; i++) {
                if (parentDir.list()[i].endsWith(".png") || parentDir.list()[i].endsWith(".jpg") || parentDir.list()[i].endsWith(".jpeg")) {
                    count++;
                    imageFilesName.add(parentDir.listFiles()[i].getAbsolutePath());
                    //System.out.println("File "+count+": "+parentDir.list()[i]);
                    if (parentDir.listFiles()[i].getAbsolutePath().equals(file.getAbsolutePath())) {
                        index = count - 1;
                        System.out.println("Match found.");
                    }
                }
            }
            System.out.println("Count: " + count);
            System.out.println("List size: " + imageFilesName.size());
            System.out.println("Index: " + index);

            if (file != null) {
                fileLabel.setText(file.getPath());
                selectedImage = new Image(file.toURI().toString());
                imageView.setImage(selectedImage);
                btnGreyScaleToggle.setSelected(false);
            } else {
                fileLabel.setText("No file selected!");
            }
        }
    }

    @FXML
    private void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void showColorChannels(ActionEvent event) throws IOException {
        if (selectedImage != null) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("rgbChannels.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("RGB Channels");
            stage.setResizable(false);
            stage.sizeToScene();
            stage.initModality(Modality.APPLICATION_MODAL);
            RgbChannelsController rgbController = loader.getController();
            rgbController.setImage(selectedImage);
            stage.show();
        } else {
            fileLabel.setText("No file selected to show color channels!");
        }
    }

    @FXML
    void showAbout() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("about.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("About");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("images/tsp-rounded-logo.png")));
        stage.show();
    }

    @FXML
    private void showPreviousImage(ActionEvent event) {
        if (!imageFilesName.isEmpty()) {
            //check for next image and show
            if (--index >= 0) {
                file = new File(imageFilesName.get(index));
                System.out.println("Showing next image at index: " + index);
                fileLabel.setText(file.getAbsolutePath());
                selectedImage = new Image(file.toURI().toString());
                imageView.setImage(selectedImage);
                btnGreyScaleToggle.setSelected(false);
            } else {
                ++index;
                fileLabel.setText("First Image.");
            }
        } else {
            fileLabel.setText("Please open a image file.");
        }
    }

    @FXML
    private void showImageDetails(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Image Details");
        alert.setHeaderText(null);
        if (file != null) {
            String filePath = file.getPath();
            alert.setContentText(filePath + "\n" + "Width = " + selectedImage.getWidth() + " Height = " + selectedImage.getHeight());
        } else {
            alert.setContentText("No file selected!");
        }
        alert.showAndWait();
    }

    @FXML
    private void toggleImageGreyScale(ActionEvent event) {
        if (selectedImage != null) {
            if (btnGreyScaleToggle.isSelected()) {
                //make the image gray within a thread
                //one way to create Runnable but not so convinient
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImage(ImageProcessor.convertToGrayImage(selectedImage));
                    }
                };
                //another way to create concurrent thread and so much handi and convenient
                Task task = createImageGrayTask();
                //Thread th = new Thread(r);
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start(); //Recomended. A new thread is started as expected.
                //th.run(); // not recomended to use.Any new thread is not started only run method is called like other simple methods.
                task.valueProperty().addListener((observable, oldValue, newValue) -> {
                    fileLabel.setText("Updating at coordinate point (X,Y): " + String.valueOf(newValue));
                });
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(task.progressProperty());

                progressIndicator.progressProperty().unbind();
                progressIndicator.progressProperty().bind(task.progressProperty());
            } else {
                //make the image colored
                imageView.setImage(selectedImage);
                fileLabel.setText(file.getPath());
            }
        } else {
            btnGreyScaleToggle.setSelected(false);
            fileLabel.setText("No file selected to conversion");
        }
    }

    @FXML
    private void showNextImage(ActionEvent event) {
//        if (imageFilesName.size() > 1 && index < imageFilesName.size() - 1) {
//            file = new File(imageFilesName.get(++index));
//            System.out.println("Showing next image at index: " + index);
//            fileLabel.setText(file.getAbsolutePath());
//            selectedImage = new Image(file.toURI().toString());
//            imageView.setImage(selectedImage);
//        }
        if (!imageFilesName.isEmpty()) {
            //check for next image and show
            if (++index < imageFilesName.size()) {
                file = new File(imageFilesName.get(index));
                System.out.println("Showing next image at index: " + index);
                fileLabel.setText(file.getAbsolutePath());
                selectedImage = new Image(file.toURI().toString());
                imageView.setImage(selectedImage);
                btnGreyScaleToggle.setSelected(false);
            } else {
                --index;
                fileLabel.setText("Last Image.");
            }
        } else {
            fileLabel.setText("Please open a image file.");
        }
    }

    private Task createImageGrayTask() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), labelGrayImageLoading);
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                progressBar.setVisible(true);
                progressIndicator.setVisible(true);
                labelGrayImageLoading.setVisible(true);
                //------Test code for blinking the label----------------
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.2);
                fadeTransition.setCycleCount(Animation.INDEFINITE);
                fadeTransition.play();
                //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                //imageView.setImage(ImageProcessor.convertToGrayImage(selectedImage));
                int selectedImageWidth = (int) selectedImage.getWidth();
                int selectedImageHeight = (int) selectedImage.getHeight();
                WritableImage result = new WritableImage(selectedImageWidth, selectedImageHeight);
                PixelReader preader = selectedImage.getPixelReader();
                PixelWriter pwriter = result.getPixelWriter();
                int count = 0;
                for (int i = 0; i < selectedImageWidth; i++) {
                    for (int j = 0; j < selectedImageHeight; j++) {
                        pwriter.setColor(i, j, preader.getColor(i, j).grayscale());
                        count++;
                        updateProgress(count, selectedImageWidth * selectedImageHeight);
                        updateValue(i + ", " + j);
                    }
                }
                imageView.setImage(result);
                //Thread.sleep(5000);
                return null;
            }

            @Override
            protected void failed() {
                super.failed();
                System.out.println("Image convertion to gray scale is failed!!!");
                btnGreyScaleToggle.setSelected(false);
                fileLabel.setText("Image convertion to gray scale is failed!!!");
                progressBar.setVisible(false);
                progressIndicator.setVisible(false);
                labelGrayImageLoading.setVisible(false);
                fadeTransition.stop();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Image convertion to gray scale is Succeed!!!");
                fileLabel.setText("Image convertion to gray scale is Succeed!!!");
                progressBar.setVisible(false);
                progressIndicator.setVisible(false);
                labelGrayImageLoading.setVisible(false);
                fadeTransition.stop();
            }
        };
    }
}
