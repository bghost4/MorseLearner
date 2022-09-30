package com.derpderphurr.morse.exercise;

public record Question<T,A> (T challenge,A answer){
    boolean isCorrect(A suppliedAnswer) {
        return answer.equals(suppliedAnswer);
    }

    public static <N> Question<N,N> echoQuestion(N thing) { return new Question(thing,thing);}
}
