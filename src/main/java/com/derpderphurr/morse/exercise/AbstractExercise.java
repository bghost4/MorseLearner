package com.derpderphurr.morse.exercise;

import java.util.function.BiConsumer;

public abstract class AbstractExercise<T,A> implements Exercise<T,A> {

    BiConsumer<Question<T,A>,A>
            onCorrect = (q,a) -> { },
            onIncorrect = (q,a) -> { };

    @Override
    public void setOnCorrect(BiConsumer<Question<T, A>, A> e) {
        this.onCorrect = e;
    }

    @Override
    public void setOnIncorrect(BiConsumer<Question<T, A>, A> a) {
        this.onIncorrect = a;
    }

    @Override
    public BiConsumer<Question<T, A>, A> getOnCorrect() {
        return onCorrect;
    }

    @Override
    public BiConsumer<Question<T, A>, A> getOnIncorrect() {
        return onIncorrect;
    }
}
