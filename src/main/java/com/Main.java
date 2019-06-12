package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/homeScreen.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Glitch");
        primaryStage.setScene(new Scene(root, 432, 219));
        primaryStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 432);
        primaryStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 219);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
