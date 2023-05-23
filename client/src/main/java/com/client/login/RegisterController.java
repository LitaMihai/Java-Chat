package com.client.login;

import com.client.chatwindow.Listener;
import com.client.util.Database;
import com.client.util.ImageCropperController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordTextField;
    @FXML private PasswordField confirmPasswordTextField;
    @FXML private TextField displayNameTextField;
    @FXML private BorderPane borderPane;
    @FXML public ImageView profileImage;

    public static Image imageToCrop;
    private double xOffset;
    private double yOffset;
    private static RegisterController instance;
    private String pathToProfileImage;
    public static Stage stageCrop;
    private static Image selectedImage;

    Logger logger = LoggerFactory.getLogger(Listener.class);

    public RegisterController() {
        instance = this;
    }

    public static RegisterController getInstance() {
        return instance;
    }

    public void setCroppedImage(Image image) {
        this.profileImage.setImage(image);
        selectedImage = image;
    }

    public void setPathToCroppedProfileImage(String path) {
        this.pathToProfileImage = path;
    }

    public void logoutScene() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent window = null;
            try {
                window = (Pane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = MainLauncher.getPrimaryStage();
            Scene scene = new Scene(window);
            stage.setMaxWidth(350);
            stage.setMaxHeight(420);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.centerOnScreen();
        });
    }

    public ImageView getImageView() {
        return this.profileImage;
    }

    public void setProfilePicture() throws IOException {
        FileChooser file = new FileChooser();
        ImageCropperController imageCropperController;
        file.setTitle("Choose a profile image");
        file.setInitialDirectory(new File(System.getProperty("user.home")));
        file.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp", "*.jpeg")
        );
        File selectedFile = file.showOpenDialog(MainLauncher.getPrimaryStage());

        if(selectedFile != null) {
            selectedImage = new Image(selectedFile.toURI().toString());

            if(selectedImage.getWidth() > 150 || selectedImage.getHeight() > 150) {
                // Start Image Cropper

                imageToCrop = selectedImage;

                Platform.runLater(() -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/ImageCropperView.fxml"));
                    Parent window = null;
                    try {
                        window = fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stageCrop = new Stage();
                    Scene scene = new Scene(window);
                    stageCrop.setResizable(false);
                    stageCrop.setScene(scene);
                    stageCrop.centerOnScreen();
                    stageCrop.show();
                });
            }
            this.pathToProfileImage = selectedFile.getAbsolutePath();
            this.profileImage.setImage(selectedImage);
            profileImage.setFitWidth(93);
            profileImage.setFitHeight(93);
            Circle clip = new Circle(93 / 2, 93 / 2, 93 / 2);
            profileImage.setClip(clip);
        }
    }

    public void cancelButtonAction() {
        logoutScene(); // Back to LoginView
    }

    public void registerButtonAction() throws IOException {
        // Checks on text fields
        if(emailTextField.getText().isEmpty()) {
            LoginController.getInstance().showErrorDialog("Email text field empty");
            logger.error("Email text field empty");
            return;
        }

        if(passwordTextField.getText().isEmpty()) {
            LoginController.getInstance().showErrorDialog("Password text field empty");
            logger.error("Password text field empty");
            return;
        }

        if(confirmPasswordTextField.getText().isEmpty()) {
            LoginController.getInstance().showErrorDialog("Confirm Password text field empty");
            logger.error("Confirm Password text field empty");
            return;
        }

        if(confirmPasswordTextField.getText().isEmpty()) {
            LoginController.getInstance().showErrorDialog("Display name text field empty");
            logger.error("Display name text field empty");
            return;
        }

        if(!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
            LoginController.getInstance().showErrorDialog("Passwords do not match");
            logger.error("Passwords do not match");
            return;
        }

        // Check if the account exists in the database
        if(Database.getInstance().checkAccount(emailTextField.getText())) {
            LoginController.getInstance().showErrorDialog("Email already used");
            logger.error("Email used");
            return;
        }

        // Create the account
        if(Database.insertAccount(emailTextField.getText(), passwordTextField.getText(), displayNameTextField.getText())) {
            Database.saveImageIntoMongoDb(this.pathToProfileImage, emailTextField.getText());
            LoginController.getInstance().showErrorDialog("Account created");
            logger.warn("Account created");
            logoutScene(); // Back to LoginView
        }

        else{
            LoginController.getInstance().showErrorDialog("Account not created");
            logger.error("Account not created");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Drag and Drop */
        borderPane.setOnMousePressed(event -> {
            xOffset = MainLauncher.getPrimaryStage().getX() - event.getScreenX();
            yOffset = MainLauncher.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            MainLauncher.getPrimaryStage().setX(event.getScreenX() + xOffset);
            MainLauncher.getPrimaryStage().setY(event.getScreenY() + yOffset);
        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });

        emailTextField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.TAB)
                passwordTextField.requestFocus();
        });

        passwordTextField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.TAB)
                confirmPasswordTextField.requestFocus();
        });

        confirmPasswordTextField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.TAB)
                displayNameTextField.requestFocus();
        });

        displayNameTextField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.TAB)
                emailTextField.requestFocus();
        });

        File defaultPhoto = new File("client/src/main/resources/images/default.png");
        this.pathToProfileImage = defaultPhoto.getAbsolutePath();

        int numberOfSquares = 30;
        while (numberOfSquares > 0){
            generateAnimation();
            numberOfSquares--;
        }
    }

    /* This method is used to generate the animation on the login window, It will generate random ints to determine
     * the size, speed, starting points and direction of each square.
     */
    public void generateAnimation(){
        Random rand = new Random();
        int sizeOfSqaure = rand.nextInt(50) + 1;
        int speedOfSqaure = rand.nextInt(10) + 5;
        int startXPoint = rand.nextInt(420);
        int startYPoint = rand.nextInt(350);
        int direction = rand.nextInt(5) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle r1 = null;

        switch (direction){
            case 1 :
                // MOVE LEFT TO RIGHT
                r1 = new Rectangle(0,startYPoint,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 -  sizeOfSqaure);
                break;
            case 2 :
                // MOVE TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;
            case 3 :
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 -  sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;
            case 4 :
                // MOVE BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,420-sizeOfSqaure ,sizeOfSqaure,sizeOfSqaure);
                moveYAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 5 :
                // MOVE RIGHT TO LEFT
                r1 = new Rectangle(420-sizeOfSqaure,startYPoint,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 6 :
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 -  sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;

            default:
                System.out.println("default");
        }

        r1.setFill(Color.web("#F89406"));
        r1.setOpacity(0.1);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speedOfSqaure * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size()-1,r1);
    }

    public void minimizeWindow(){
        MainLauncher.getPrimaryStage().setIconified(true);
    }

    public void closeSystem(){
        Platform.exit();
        System.exit(0);
    }
}
