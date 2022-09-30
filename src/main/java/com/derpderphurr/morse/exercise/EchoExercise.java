package com.derpderphurr.morse.exercise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class EchoExercise<T> extends AbstractExercise<T,T> {

    private final String name;
    private final Random rand;
    private List<Question<T,T>> pool = new ArrayList<>();

    public EchoExercise(String name, Collection<Question<T,T>> pool) {
        this(name,pool,System.currentTimeMillis());
    }

    public EchoExercise(String name, Collection<Question<T,T>> pool,long seed) {
        this.pool.addAll(pool);
        this.name = name;
        this.rand = new Random(seed);
    }

    @Override
    public Question<T, T> getQuestion() {
        return pool.get(rand.nextInt(pool.size()-1));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() { return getName(); }
}
