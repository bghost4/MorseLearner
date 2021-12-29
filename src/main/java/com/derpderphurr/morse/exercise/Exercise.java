package com.derpderphurr.morse.exercise;

import java.util.function.BiConsumer;

public interface Exercise<T,A> {

    String getName();

    void setOnCorrect(BiConsumer<Question<T,A>,A> e);
    void setOnIncorrect(BiConsumer<Question<T,A>,A> a);

    BiConsumer<Question<T,A>,A> getOnCorrect();
    BiConsumer<Question<T,A>,A> getOnIncorrect();

    Question<T,A> getQuestion();

    default void submitAnswer(Question<T,A> q,A answer) {
        if(q.isCorrect(answer)) { getOnCorrect().accept(q,answer);}
        else { getOnIncorrect().accept(q,answer); }
    }

}
