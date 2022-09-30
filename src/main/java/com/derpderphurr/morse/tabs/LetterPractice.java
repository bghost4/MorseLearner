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
import javafx.application.Platform;
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

    private Question<String,String> currentQuestion;

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
        tfBody.getChildren().clear();
        if(cboSelect.getValue() != null) {
            currentQuestion = cboSelect.getValue().getQuestion();
            this.getTabPane().getScene().setOnKeyPressed(ke -> {
                System.out.println("Got Key Text: "+ke.getText());
                Text input = new Text(ke.getText());
                input.setFont(Font.font("Verdana",25));
                if(!ke.getText().equals(currentQuestion.answer())) {
                    input.setFill(Color.RED);
                    tfBody.getChildren().add(input);
                    player.queueMessage(new Playable(currentQuestion.challenge()));
                } else {
                    input.setFill(Color.GREEN);
                    tfBody.getChildren().add(input);

                    //for some reason this causes a 2 second delay, then shows the letter and immediatly plays the sound.
                    Platform.runLater(() -> {
                        currentQuestion = cboSelect.getValue().getQuestion();
                        try {
                            Thread.sleep(2000); // Wait 2 seconds before sending the next letter
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        player.queueMessage(new Playable(currentQuestion.challenge()));
                    });

                }
            });

            player.queueMessage(new Playable(currentQuestion.challenge()));
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

        List<Question<String,String>> anteos_pool = List.of(
            Question.echoQuestion("a"),
            Question.echoQuestion("n"),
            Question.echoQuestion("t"),
            Question.echoQuestion("e"),
            Question.echoQuestion("o"),
            Question.echoQuestion("s")
        );

        EchoExercise<String> anteos = new EchoExercise<>("ANTEOS",anteos_pool);
        cboSelect.getItems().add(anteos);

        btnGo.setOnAction(this::begin);

    }
}




