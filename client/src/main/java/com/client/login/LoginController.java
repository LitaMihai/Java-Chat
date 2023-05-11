package com.client.login;

import com.client.chatwindow.ChatController;
import com.client.chatwindow.Listener;
import com.client.util.Database;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private StackPane lastAccount;
    @FXML private ImageView lastAccountPhoto;
    @FXML private Label titleLabel;
    @FXML private Label emailLabel;
    @FXML private Label passLabel;
    @FXML private GridPane gridPane;
    @FXML public  TextField hostnameTextField;
    @FXML private TextField portTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordTextField;
    public static ChatController con;
    @FXML private BorderPane borderPane;
    private double xOffset;
    private double yOffset;
    private Scene scene;
    String username;
    Logger logger = LoggerFactory.getLogger(Listener.class);
    private static LoginController instance;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }

    public void connectWithLastAccount() {
        String email = null;
        String hostname = hostnameTextField.getText();
        int port = Integer.parseInt(portTextField.getText());

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("lastAccount.json")) {
            Object obj = jsonParser.parse(reader);

            JSONObject account = (JSONObject) obj;

            email = (String) account.get("email");
            username = Database.getName(email); // Get the username from the database

            // Connect to the server
            logIn(hostname, port, username);

        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        } catch (ParseException e) {
            return;
        }
    }

    public void loginButtonAction() throws IOException {
        String hostname = hostnameTextField.getText();
        int port = Integer.parseInt(portTextField.getText());
        String email = emailTextField.getText();
        username = Database.getName(email); // Get the username from the database
        String password = passwordTextField.getText();

        // Account verification
        if(!Database.getAccount(email, password)){ // If the account does not exist
            LoginController.getInstance().showErrorDialog("Email or password is incorrect");
            logger.error("Wrong Account");
            return;
        }

        // Write in json the account information for further easy login
        writeAccountInJson(email, password);

        // Connect to the server
        logIn(hostname, port, username);
    }

    private void logIn(String hostname, int port, String username) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/ChatView.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        con = fmxlLoader.<ChatController>getController();
        Listener listener = new Listener(hostname, port, username, con);
        Thread x = new Thread(listener);
        x.start();
        Stage stage = MainLauncher.getPrimaryStage();
        stage.setMinWidth(1040);
        stage.setMinHeight(620);
        stage.setMaxWidth(1040);
        stage.setMaxHeight(620);
        this.scene = new Scene(window);
    }

    private void writeAccountInJson(String email, String password){
        JSONObject obj = new JSONObject();
        obj.put("email", email);
        obj.put("password", password);

        try (FileWriter file = new FileWriter("lastAccount.json")){
            // Write in file
            file.write(obj.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerButtonAction(){
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/RegisterView.fxml"));
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

    public void showScene() throws IOException {
        Platform.runLater(() -> {
            Stage stage = (Stage) hostnameTextField.getScene().getWindow();
            stage.setResizable(true);

            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(this.scene);
            stage.centerOnScreen();
            con.setUsernameLabel(username);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                emailTextField.requestFocus();
        });

        // Check if the user has logged in before
        if(firstConnect() == true){
            // Remove the last account photo
            gridPane.getChildren().remove(lastAccount);

            // Move the text fields and labels accordingly
            GridPane.setConstraints(titleLabel, 1, 2);
            GridPane.setConstraints(emailLabel, 0, 3);
            GridPane.setConstraints(emailTextField, 1, 3);
            GridPane.setConstraints(passLabel, 0, 4);
            GridPane.setConstraints(passwordTextField, 1, 4);
        }
        else {
            // Get the last account photo
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader("lastAccount.json")){
                // Read the json file
                Object obj = jsonParser.parse(reader);
                JSONObject account = (JSONObject) obj;

                String email = (String) account.get("email");

                Image profileImage = new Image(Database.getImageFromMongoDB(email));
                this.lastAccountPhoto.setImage(profileImage);
                this.lastAccountPhoto.setFitWidth(93);
                this.lastAccountPhoto.setFitHeight(93);
                Circle clip = new Circle(93 / 2, 93 / 2, 93 / 2);
                this.lastAccountPhoto.setClip(clip);

            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } catch (ParseException e) {
            }
        }

        int numberOfSquares = 30;
        while (numberOfSquares > 0){
            generateAnimation();
            numberOfSquares--;
        }
    }

    private boolean firstConnect(){
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("lastAccount.json"))
        {
            // Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject account = (JSONObject) obj;

            String email = (String) account.get("email");

            // Check if the file is empty
            if(email.isEmpty())
                return true;

            return false;

        } catch (FileNotFoundException e) {
            return true;
        } catch (IOException e) {
            return true;
        } catch (ParseException e) {
            return true;
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

    /* Terminates Application */
    public void closeSystem(){
        Platform.exit();
        System.exit(0);
    }

    public void minimizeWindow(){
        MainLauncher.getPrimaryStage().setIconified(true);
    }

    /* This displays an alert message to the user */
    public void showErrorDialog(String message) {
        Platform.runLater(()-> {
            Alert alert;
            switch(message){
                case "Could not connect to server":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please check for firewall issues and check if the server is running.");
                    alert.showAndWait();
                    break;

                case "Email or password is incorrect":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please check the email and password you have entered.");
                    alert.showAndWait();
                    break;

                case "Email already used":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please use another email address.");
                    alert.showAndWait();
                    break;

                case "Email text field empty":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please enter an email address.");
                    alert.showAndWait();
                    break;

                case "Password text field empty":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please enter a password.");
                    alert.showAndWait();
                    break;

                case "Display name text field empty":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please enter a name.");
                    alert.showAndWait();
                    break;

                case "Confirm Password text field empty":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please confirm your password.");
                    alert.showAndWait();
                    break;

                case "Passwords do not match":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please make sure your passwords match.");
                    alert.showAndWait();
                    break;

                case "Account created":
                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmed!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please login to continue.");
                    alert.showAndWait();
                    break;

                case "Account not created":
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText(message);
                    alert.setContentText("Please try again.");
                    alert.showAndWait();
                    break;
            }
        });
    }
}
