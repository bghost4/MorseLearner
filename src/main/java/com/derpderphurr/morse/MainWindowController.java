package com.derpderphurr.morse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends VBox {

    private PlayerThread player;

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
    private void playAlphabet(ActionEvent e) {
        PlayAlphabet t = new PlayAlphabet(player);
        t.start();
    }

    @FXML
    private void onAbout(ActionEvent e) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);

        a.setHeaderText("MorseLearner Info an Credits");
        String sb = "Developed with Adopt OpenJDK-14.02 <https://adoptopenjdk.net/>\n" +
                "Jetbrains IntellaJ <https://www.jetbrains.com/idea/> \n" +
                "JavaFX <https://openjfx.io/>\n" +
                "Gradle <https://gradle.org/>\n\n" +
                "Special Thanks goes out to user tim.kahn @ freesound.org for creating Phonetic Alphabet\n" +
                "Special Thanks to Amy Gedgaudas for providing their voice" +
                "\t<https://freesound.org/people/tim.kahn/packs/14153/>";
        a.setContentText(sb);

        a.showAndWait();
    }

    @FXML
    private void clearPlayText(ActionEvent e) {
        txtPlayRender.clear();
        txtPlayText.clear();
    }

    @FXML
    private void playText(ActionEvent e) {
        txtPlayRender.clear();
        player.queueMessage(new Playable(txtPlayText.getText(),() -> {}, (cc) -> txtPlayRender.appendText(cc.getCha())));
    }

    public void shutdown() { player.shutdown(); }

    @FXML
    private TextArea txtPlayText;

    @FXML
    private TextArea txtPlayRender;

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
            player.queueMessage(new Playable(str));
        }
    }

    @FXML
    void initialize() {
        assert txtWPM != null : "fx:id=\"txtWPM\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert txtToneFreq != null : "fx:id=\"txtToneFreq\" was not injected: check your FXML file 'mainwindow.fxml'.";
        assert sldVolume != null : "fx:id=\"sldVolume\" was not injected: check your FXML file 'mainwindow.fxml'.";


        player = new PlayerThread();
        player.setDaemon(true);
        player.start();

        sldVolume.valueProperty().bindBidirectional(player.volumeProperty());
        txtWPM.setText(""+player.wpmProperty().get());
        txtWPM.textProperty().addListener((ob,ov,nv) -> {
            try{
                int value = Integer.parseInt(nv);
                player.wpmProperty().set(value);
            }catch(NumberFormatException nfe) {

            }
        });

        txtToneFreq.setText(""+player.toneProperty().get());
        txtToneFreq.textProperty().addListener((ob,ov,nv) -> {
            try{
                int value = Integer.parseInt(nv);
                player.toneProperty().set(value);
            }catch(NumberFormatException nfe) {

            }
        });



    }

}
