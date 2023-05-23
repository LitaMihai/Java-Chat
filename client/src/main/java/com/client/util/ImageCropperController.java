package com.client.util;

import com.client.login.MainLauncher;
import com.client.login.RegisterController;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageCropperController implements Initializable {

    private WritableImage selectedImage;
    @FXML public Circle circle;
    @FXML public ImageView profileImageView;
    @FXML public Pane pane;

    private double initialX = 0;
    private double initialY = 0;
    private double translateX = 0;
    private double translateY = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.profileImageView.setImage(RegisterController.imageToCrop);

        this.circle.setRadius(this.profileImageView.getImage().getWidth()/4);

        enableImageCropper();
    }

    public void cropImage() throws IOException {
        int cropWidth = (int) (circle.getRadius() * 2);
        int cropHeight = (int) (circle.getRadius() * 2);

        this.selectedImage = new WritableImage(cropWidth, cropHeight);

        PixelReader pixelReader = this.profileImageView.getImage().getPixelReader();
        PixelWriter pixelWriter = this.selectedImage.getPixelWriter();

        int startX = (int) (circle.getTranslateX() + (profileImageView.getImage().getWidth()/2));
        int startY = (int) (circle.getTranslateY() + (profileImageView.getImage().getHeight()/2));

        for (int y = 0; y < cropHeight; y++) {
            for (int x = 0; x < cropWidth; x++) {
                int sourceX = startX - (cropWidth / 2) + x;
                int sourceY = startY - (cropHeight / 2) + y;
                int color = pixelReader.getArgb(sourceX, sourceY);
                pixelWriter.setArgb(x, y, color);
            }
        }
        this.saveImage(this.selectedImage);
        RegisterController.getInstance().setPathToCroppedProfileImage("croppedProfilePicture.png");
        RegisterController.getInstance().setCroppedImage(this.selectedImage);
    }

    private void enableImageCropper() {
        circle.setOnMousePressed(event -> {
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            translateX = circle.getTranslateX();
            translateY = circle.getTranslateY();
        });

        circle.setOnMouseDragged(event -> {
            circle.setCursor(Cursor.CLOSED_HAND);
            double offsetX = event.getSceneX() - initialX;
            double offsetY = event.getSceneY() - initialY;

            circle.setTranslateX(translateX + offsetX);
            circle.setTranslateY(translateY + offsetY);
        });

        circle.setOnMouseReleased(event -> {
            circle.setCursor(Cursor.OPEN_HAND);
        });
    }

    private void saveImage(Image image) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("croppedProfilePicture.png"));
    }

    public void cancelButton(){
        Stage stage = (Stage) circle.getScene().getWindow();
        stage.close();
    }
}
