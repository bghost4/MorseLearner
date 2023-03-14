package com.derpderphurr.morse.tabs;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.derpderphurr.morse.Playable;
import com.derpderphurr.morse.PlayerThread;
import com.derpderphurr.morse.exercise.EchoExercise;
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
    private Button btnAgn;

    @FXML
    private Button btnGiveUp;

    private boolean hasBegun = false;

    @FXML
    private TextFlow tfBody;

    private PlayerThread player;

    private Question<String,String> currentQuestion;

    private int misscount = 0;

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
        this.hasBegun = true;

        if(cboSelect.getValue() != null) {
            currentQuestion = cboSelect.getValue().getQuestion();
            this.getTabPane().getScene().setOnKeyPressed(ke -> {
                if (hasBegun) {
                    System.out.println("Got Key Text: " + ke.getText());
                    Text input = new Text(ke.getText());
                    input.setFont(Font.font("Verdana", 25));
                    if (!ke.getText().equals(currentQuestion.answer())) {
                        input.setFill(Color.RED);
                        if(misscount > 3) {
                            misscount = 0;
                            input.setText(String.format("(%s)",currentQuestion.answer()));
                        }
                        tfBody.getChildren().add(input);
                        player.queueMessage(new Playable(currentQuestion.challenge()));
                        misscount++;
                    } else {
                        misscount = 0;
                        input.setFill(Color.GREEN);
                        tfBody.getChildren().add(input);
                        currentQuestion = cboSelect.getValue().getQuestion();
                        player.queueMessage(new Playable(currentQuestion.challenge()));
                    }
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

        List<Question<String,String>> alphabet_pool = List.of(
                Question.echoQuestion("a"),
                Question.echoQuestion("b"),
                Question.echoQuestion("c"),
                Question.echoQuestion("d"),
                Question.echoQuestion("e"),
                Question.echoQuestion("f"),
                Question.echoQuestion("g"),
                Question.echoQuestion("h"),
                Question.echoQuestion("i"),
                Question.echoQuestion("j"),
                Question.echoQuestion("k"),
                Question.echoQuestion("l"),
                Question.echoQuestion("m"),
                Question.echoQuestion("n"),
                Question.echoQuestion("o"),
                Question.echoQuestion("p"),
                Question.echoQuestion("q"),
                Question.echoQuestion("r"),
                Question.echoQuestion("s"),
                Question.echoQuestion("t"),
                Question.echoQuestion("u"),
                Question.echoQuestion("v"),
                Question.echoQuestion("w"),
                Question.echoQuestion("x"),
                Question.echoQuestion("y"),
                Question.echoQuestion("z")
        );

        List<Question<String,String>> code_3_pool = List.of(
                Question.echoQuestion("s"),
                Question.echoQuestion("u"),
                Question.echoQuestion("r"),
                Question.echoQuestion("w"),
                Question.echoQuestion("d"),
                Question.echoQuestion("k"),
                Question.echoQuestion("g"),
                Question.echoQuestion("o")
        );

        List<Question<String,String>> code_4_pool = List.of(
                Question.echoQuestion("h"),
                Question.echoQuestion("v"),
                Question.echoQuestion("f"),
                Question.echoQuestion("l"),
                Question.echoQuestion("p"),
                Question.echoQuestion("j"),
                Question.echoQuestion("b"),
                Question.echoQuestion("x"),
                Question.echoQuestion("c"),
                Question.echoQuestion("y"),
                Question.echoQuestion("z"),
                Question.echoQuestion("q")
        );


        EchoExercise<String> anteos = new EchoExercise<>("ANTEOS",anteos_pool);
        EchoExercise<String> alphabet = new EchoExercise<>("Alphabet",alphabet_pool);
        EchoExercise<String> code3 = new EchoExercise<>("Code 3",code_3_pool);
        EchoExercise<String> code4 = new EchoExercise<>("Code 4",code_4_pool);
        cboSelect.getItems().addAll(anteos,alphabet,code3,code4);

        btnGo.setOnAction(this::begin);
        btnAgn.setOnAction( eh -> player.queueMessage(new Playable(currentQuestion.challenge())) );




    }
}




