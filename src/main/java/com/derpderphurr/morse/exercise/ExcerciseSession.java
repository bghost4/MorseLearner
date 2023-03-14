package com.derpderphurr.morse.exercise;

import java.util.ArrayList;
import java.util.List;

public class ExcerciseSession<T,A> {
    record Response<T,A>(Question<T,A> challenge,A response){};
    record QuestionStats(int seenCount,int correctCount){};

    private List<Response<T,A>> results = new ArrayList<>();



}
