package com.derpderphurr.morse.tabs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.derpderphurr.morse.PlayerThread;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.text.TextFlow;

public class LetterPractice extends Tab {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<?> cboSelect;

    @FXML
    private Button btnGo;

    @FXML
    private TextFlow tfBody;

    private PlayerThread player;

    public LetterPractice() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LetterPractice.fxml"));
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

    @FXML
    void initialize() {
        assert cboSelect != null : "fx:id=\"cboSelect\" was not injected: check your FXML file 'LetterPractice.fxml'.";
        assert btnGo != null : "fx:id=\"btnGo\" was not injected: check your FXML file 'LetterPractice.fxml'.";
        assert tfBody != null : "fx:id=\"tfBody\" was not injected: check your FXML file 'LetterPractice.fxml'.";
    }
}




