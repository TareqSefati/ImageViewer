package com.tareq.imageviewer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class RgbChannelsController implements Initializable {

    private Image sourceImage;
    @FXML
    private ImageView redImageView;
    @FXML
    private ImageView greenImageView;
    @FXML
    private ImageView blueImageView;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label labelLoading;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setVisible(false);
        labelLoading.setVisible(false);
    }

    public void setImage(Image image) {
        this.sourceImage = image;
        Task task = getRGBchannelsTask();
        Thread th = new Thread(task);
        th.setDaemon(true);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(task.progressProperty());
        th.start();
    }

    private Task getRGBchannelsTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                progressBar.setVisible(true);
                labelLoading.setVisible(true);
                PixelReader pr = sourceImage.getPixelReader();
                int width = (int) sourceImage.getWidth();
                int height = (int) sourceImage.getHeight();

                WritableImage redResult = new WritableImage(width, height);
                WritableImage greenResult = new WritableImage(width, height);
                WritableImage blueResult = new WritableImage(width, height);

                int count = 0;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color col = pr.getColor(x, y);
                        count++;
                        redResult.getPixelWriter().setColor(x, y, new Color(col.getRed(), 0, 0, 1.0));
                        greenResult.getPixelWriter().setColor(x, y, new Color(0, col.getGreen(), 0, 1.0));
                        blueResult.getPixelWriter().setColor(x, y, new Color(0, 0, col.getBlue(), 1.0));
                        updateProgress(count, width*height);
                    }
                }
                redImageView.setImage(redResult);
                greenImageView.setImage(greenResult);
                blueImageView.setImage(blueResult);
                
                return null;
            }

            @Override
            protected void failed() {
                super.failed();
                progressBar.setVisible(false);
                labelLoading.setVisible(false);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                progressBar.setVisible(false);
                labelLoading.setVisible(false);
            }
        };
    }
}
