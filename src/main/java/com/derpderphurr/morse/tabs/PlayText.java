package com.derpderphurr.morse.tabs;

import com.derpderphurr.morse.Playable;
import com.derpderphurr.morse.PlayerThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class PlayText extends Tab {

    @FXML
    private TextArea txtPlayText;

    @FXML
    private TextArea txtPlayRender;

    private PlayerThread player;

    public PlayText() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/playText.fxml"));
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
    private void clearPlayText(ActionEvent e) {
        txtPlayRender.clear();
        txtPlayText.clear();
    }

    @FXML
    private void playText(ActionEvent e) {
        txtPlayRender.clear();
        player.queueMessage(new Playable(txtPlayText.getText(),() -> {}, (cc) -> txtPlayRender.appendText(cc.getCha())));
    }


}
