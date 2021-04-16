package com.derpderphurr.morse;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class PlayerThread extends Thread {

    private final SimpleBooleanProperty playing = new SimpleBooleanProperty(false);
    private final SimpleIntegerProperty wpm = new SimpleIntegerProperty(20);
    private final SimpleIntegerProperty tone = new SimpleIntegerProperty(660);
    private final SimpleDoubleProperty volume = new SimpleDoubleProperty(50);
    private final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(false);

    private SourceDataLine line;
    private final int sampleRate = 44100;
    private final AudioFormat defaultAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 1, 2, sampleRate, true);

    private boolean quit = false;
    private boolean cancelPlayback = false;

    public void cancelPlayback() {
        cancelPlayback = true;
    }

    public void queueMessage(PlayJob p) {
        queue.add(p);
    }

    public void shutdown() {
        quit = true;
    }

    private final ArrayBlockingQueue<PlayJob> queue = new ArrayBlockingQueue<>(20);

    private void playCodeElement(CodeParticle thisElement, ByteBuffer bb) {
        int numSamples = Codec.timeUnitsToNumSamples(thisElement.units, wpm.get(), sampleRate);

        for (int i = 0; i < numSamples; i++) {
            if (thisElement.type == CodeParticle.Type.SPACE) {
                bb.putShort((short) 0);
            } else {
                double angle = 2.0 * Math.PI * i / ((double) sampleRate / (double) tone.get());
                double out = (Math.sin(angle) * volume.get());
                bb.putShort((short) out);
            }
            line.write(bb.array(), 0, bb.limit());
            bb.rewind();
        }
    }

    @Override
    public void run() {
        try {
            line = AudioSystem.getSourceDataLine(defaultAudioFormat);
            line.open(defaultAudioFormat);
            line.start();
            Platform.runLater(() -> isRunning.set(true));
        }catch(LineUnavailableException e) {
            quit = true;
        }

        while (!quit) {
            try {
                if( queue.isEmpty() ) { cancelPlayback = false; }
                PlayJob myJob = queue.take();
                var data = Codec.translateString(myJob.getMessage());

                ByteBuffer bb = ByteBuffer.allocate(2); //Allocate buffer up here to keep it from re-allocating in loop

                for(CodeCharacter cc : data) {
                    Platform.runLater(() -> myJob.getReporter().accept(cc));
                    for(CodeParticle particle : cc.particles) {
                        if(cancelPlayback) { break; }
                        playCodeElement(particle,bb);
                    }
                    if(cancelPlayback) { break; }
                }

                Platform.runLater(myJob.getOnFinished());

            } catch (InterruptedException e) {
                quit = true;
            }
        } // while(!quit)
        Platform.runLater(() -> isRunning.set(false));
    }
}
