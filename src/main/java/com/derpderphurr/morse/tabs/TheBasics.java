package com.derpderphurr.morse.tabs;

import com.derpderphurr.morse.PlayAlphabet;
import com.derpderphurr.morse.Playable;
import com.derpderphurr.morse.PlayerThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;

import java.io.IOException;

public class TheBasics extends Tab {

    private PlayerThread player;

    public TheBasics() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/theBasics.fxml"));
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
    void onLearnButton(ActionEvent event) {
        Object obj = event.getSource();
        if(obj instanceof Button) {
            String str = ((Button)obj).getText();
            player.queueMessage(new Playable(str));
        }
    }

    @FXML
    void onPlayAlphabet(ActionEvent e) {
        PlayAlphabet t = new PlayAlphabet(player);
        t.start();
    }

}
