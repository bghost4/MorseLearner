package com.derpderphurr.morse.tabs;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.derpderphurr.morse.Playable;
import com.derpderphurr.morse.PlayerThread;
import com.derpderphurr.morse.exercise.EchoExercise;
import com.derpderphurr.morse.exercise.Exercise;
import com.derpderphurr.morse.exercise.Question;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LetterPractice extends Tab {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<EchoExercise<String>> cboSelect;

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

    private void begin(ActionEvent actionEvent) {
        if(cboSelect.getValue() != null) {
            Question<String, String> q = cboSelect.getValue().getQuestion();
            this.getTabPane().getScene().setOnKeyPressed(ke -> {
                System.out.println("Got Key Text: "+ke.getText());
                Text input = new Text(ke.getText());
                input.setFont(Font.font("Verdana",25));
                if(!ke.getText().equals(q.getAnswer())) {
                    input.setFill(Color.RED);
                    player.queueMessage(new Playable(q.getChallenge()));
                } else {
                    input.setFill(Color.GREEN);
                }
                tfBody.getChildren().add(input);
            });
            player.queueMessage(new Playable(q.getChallenge()));
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

        List<EchoExercise.EchoQuestion<String>> anteos_pool = List.of(
            new EchoExercise.EchoQuestion<>("a"),
            new EchoExercise.EchoQuestion<>("n"),
            new EchoExercise.EchoQuestion<>("t"),
            new EchoExercise.EchoQuestion<>("e"),
            new EchoExercise.EchoQuestion<>("o"),
            new EchoExercise.EchoQuestion<>("s")
        );

        EchoExercise<String> anteos = new EchoExercise<>("ANTEOS",anteos_pool);
        cboSelect.getItems().add(anteos);

        btnGo.setOnAction(this::begin);

    }
}




