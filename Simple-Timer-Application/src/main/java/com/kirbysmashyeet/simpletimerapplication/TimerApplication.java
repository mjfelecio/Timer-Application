package com.kirbysmashyeet.simpletimerapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TimerApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimerApplication.class.getResource("timer.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        stage.setTitle("Simple Timer Application");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


}