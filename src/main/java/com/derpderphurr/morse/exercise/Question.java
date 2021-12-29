package com.derpderphurr.morse.exercise;

public class Question<T,A> {
    private T challenge;
    private A answer;

    public Question(T q,A a) {
        challenge = q;
        answer = a;
    }

    boolean isCorrect(A suppliedAnswer) {
        return answer.equals(suppliedAnswer);
    }

    public T getChallenge() {
        return challenge;
    }

    public A getAnswer() {
        return answer;
    }
}
