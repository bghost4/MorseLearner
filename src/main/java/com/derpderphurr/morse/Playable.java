package com.derpderphurr.morse;

import javax.sound.sampled.AudioInputStream;
import java.util.function.Consumer;

public class Playable {

    private final String message;
    private final Runnable onFinished;
    private final Consumer<CodeCharacter> reporter;
    private final AudioInputStream sampleData;

    public AudioInputStream getSampleData() {
        return sampleData;
    }

    public enum Type { CODE,SAMPLES }
    public final Type type;

    public Playable(String message, Runnable onFinished, Consumer<CodeCharacter> reporter) {
        this.message = message;
        this.onFinished = onFinished;
        this.reporter = reporter;
        this.type = Type.CODE;
        this.sampleData = null;
    }

    public Playable(AudioInputStream ais) {
        this.message = "";
        this.onFinished = () -> {};
        this.reporter = (c) -> {};
        this.type = Type.SAMPLES;
        this.sampleData = ais;
    }

    public Playable(String message) {
        this(message,() -> {},(cc) -> {});
    }

    public String getMessage() {
        return message;
    }

    public Runnable getOnFinished() {
        return onFinished;
    }

    public Consumer<CodeCharacter> getReporter() {
        return reporter;
    }
}
