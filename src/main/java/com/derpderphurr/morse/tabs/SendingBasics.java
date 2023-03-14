package com.derpderphurr.morse.tabs;

import com.derpderphurr.morse.Codec;
import com.derpderphurr.morse.PlayerThread;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import java.io.IOException;

public class SendingBasics extends Tab {

    private PlayerThread player;

    private boolean keyPressed = false;

    @FXML
    private TextArea decodeArea;

    @FXML
    private Button btnReset;

    private long lastEvent = System.currentTimeMillis();

    private EventHandler<InputEvent>
            keyPressHandler = (in) -> { keyPressed = true; lastEvent = System.currentTimeMillis(); },
            keyReleaseHandler = (in) -> {
                    keyPressed = false;
                    long lastOnEvent = System.currentTimeMillis() - lastEvent;
                    lastEvent = System.currentTimeMillis();
                    System.out.printf("Key was on for %dl Millis\n",lastOnEvent);

                    double approxTimeUnits = (double)lastOnEvent / (double)Codec.WPMToMsPerTimeUnit(player.wpmProperty().get());
                    if(approxTimeUnits > 2.6) {
                        decodeArea.appendText("-");
                    } else {
                        decodeArea.appendText(".");
                    }
            };

    public boolean isKeyPressed() { return keyPressed; }

    @FXML
    void initialize() {
        this.selectedProperty().addListener((ob,ov,nv) -> {
            if(nv) {
                this.getTabPane().getScene().addEventFilter(KeyEvent.KEY_PRESSED,keyPressHandler);
                this.getTabPane().getScene().addEventFilter(KeyEvent.KEY_RELEASED,keyReleaseHandler);
                player.setOscOnFunct((v) -> keyPressed);
                player.oscillatorModeProperty().set(true);
                System.out.println("Setting Oscillator mode to on");
            } else {
                this.getTabPane().getScene().removeEventFilter(KeyEvent.KEY_PRESSED,keyPressHandler);
                this.getTabPane().getScene().removeEventFilter(KeyEvent.KEY_RELEASED,keyReleaseHandler);
                player.oscillatorModeProperty().set(false);
                System.out.println("Setting Oscillator mode to off");
            }
        });
    }

    public SendingBasics() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sendingBasics.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to Load FXML",exception);
        }
    }

    public PlayerThread getPlayer() {
        return player;
    }

    public void setPlayer(PlayerThread player) {
        this.player = player;
    }

}
