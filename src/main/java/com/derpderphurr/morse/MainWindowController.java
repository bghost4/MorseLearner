package com.derpderphurr.morse;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends VBox {

    private PlayerService player;

    public MainWindowController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainwindow.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to Load FXML",exception);
        }
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField txtWPM;

    @FXML
    private TextField txtToneFreq;

    @FXML
    private Slider sldVolume;

    @FXML
    void onLearnButton(ActionEvent event) {
        Object obj = event.getSource();
        if(obj instanceof Button) {
            String str = ((Button)obj).getText();
            player.playString(str);
        }
    }

    @FXML
    void initialize() {
        assert txtWPM != null : "fx:id=\"txtWPM\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert txtToneFreq != null : "fx:id=\"txtToneFreq\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert sldVolume != null : "fx:id=\"sldVolume\" was not injected: check your FXML file 'mainwindow.fxml'.";

        Platform.runLater(() -> {
        player = new PlayerService();
        player.setOnFailed(wse -> System.out.println("Player Task Failed"));
        player.setOnSucceeded(wse -> System.out.println("Player Task Finished Successfully"));
        player.setOnCancelled(wse -> System.out.println("Player Task Was Cancelled"));
        player.setOnSucceeded(wse -> System.out.println("Player Task Finished Successfully"));
        player.start();
        });

        sldVolume.valueProperty().addListener((ob,ov,nv) -> player.volumeProperty().set(nv.intValue()));
        txtWPM.textProperty().addListener((ob,ov,nv) -> {
            try{
                int value = Integer.parseInt(nv);
                player.volumeProperty().set(value);
            }catch(NumberFormatException nfe) {

            }
        });

        txtToneFreq.textProperty().addListener((ob,ov,nv) -> {
            try{
                int value = Integer.parseInt(nv);
                player.toneFreqProperty().set(value);
            }catch(NumberFormatException nfe) {

            }
        });



    }

}
