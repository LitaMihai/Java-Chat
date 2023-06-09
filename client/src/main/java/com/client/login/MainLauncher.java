package com.client.login;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainLauncher extends Application {
    private static Stage primaryStageObj;

    @Override
    public void start(Stage primaryStage) throws IOException {
        MainLauncher.primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(this.getClass().getResource("/views/LoginView.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Socket Chat : Client Version 0.5");
        primaryStage.getIcons().add(new Image(this.getClass().getResource("/images/icon.png").toString()));
        Scene mainScene = new Scene(root, 350, 420);
        mainScene.setRoot(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return MainLauncher.primaryStageObj;
    }
}
