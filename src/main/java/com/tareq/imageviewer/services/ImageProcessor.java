package com.tareq.imageviewer.services;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageProcessor {
    public static Image convertToGrayImage(Image coloredImage) {
        WritableImage result = new WritableImage((int) coloredImage.getWidth(), (int) coloredImage.getHeight());
        PixelReader preader = coloredImage.getPixelReader();
        PixelWriter pwriter = result.getPixelWriter();

        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {
                pwriter.setColor(i, j, preader.getColor(i, j).grayscale());
            }
        return result;
    }
}
