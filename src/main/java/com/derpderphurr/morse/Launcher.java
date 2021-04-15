package com.derpderphurr.morse;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MainWindowController p = new MainWindowController();

        primaryStage.setOnCloseRequest(eh -> {
            //TODO implement window closing event
        });

        if(p == null) {
            System.exit(1);
        } else {
            Scene scene = new Scene(p);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Learn Morse Code");
            primaryStage.show();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}