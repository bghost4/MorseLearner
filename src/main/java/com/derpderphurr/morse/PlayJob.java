package com.derpderphurr.morse;

import java.util.function.Consumer;

public class PlayJob {

    private final String message;
    private final Runnable onFinished;
    private final Consumer<CodeCharacter> reporter;


    public PlayJob(String message, Runnable onFinished, Consumer<CodeCharacter> reporter) {
        this.message = message;
        this.onFinished = onFinished;
        this.reporter = reporter;
    }

    public PlayJob(String message) {
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
