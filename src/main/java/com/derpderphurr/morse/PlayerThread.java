package com.derpderphurr.morse;

import javafx.application.Platform;
import javafx.beans.property.*;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PlayerThread extends Thread {

    private final SimpleBooleanProperty playing = new SimpleBooleanProperty(false); // true is playing
    private final SimpleIntegerProperty wpm = new SimpleIntegerProperty(20); // value for words per minute
    private final SimpleIntegerProperty tone = new SimpleIntegerProperty(660); //Value in hz of Tone
    private final SimpleDoubleProperty volume = new SimpleDoubleProperty(0.75); //probably should rescale this to the bounds of a short
    private final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(false);

    public IntegerProperty wpmProperty() { return wpm; }
    public IntegerProperty toneProperty() { return tone; }
    public DoubleProperty volumeProperty() { return volume; }
    public ReadOnlyBooleanProperty isRunningProperty() { return isRunning; }
    public ReadOnlyBooleanProperty isPlayingProperty() { return playing; }

    private SimpleBooleanProperty oscillatorMode = new SimpleBooleanProperty(false);

    public synchronized BooleanProperty oscillatorModeProperty() { return oscillatorMode; }

    private SourceDataLine line;
    private static final int sampleRate = 44100;
    private static final AudioFormat defaultAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 1, 2, sampleRate, false);

    private Predicate<Void> oscOnFunct = (v) -> false;

    private boolean quit = false;
    private boolean cancelPlayback = false;

    public void cancelPlayback() {
        cancelPlayback = true;
    }

    public void queueMessage(Playable p) {
        try {
            if(!cancelPlayback) {
                queue.offer(p, 30, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setOscOnFunct(Predicate<Void> func) {
        this.oscOnFunct = func;
    }

    public void shutdown() {
        quit = true;
    }

    private final ArrayBlockingQueue<Playable> queue = new ArrayBlockingQueue<>(20);

    private void insertCodeElement(CodeParticle thisElement, ByteBuffer bb) {
        int numSamples = Codec.timeUnitsToNumSamples(thisElement.units, wpm.get(), sampleRate);

        double pre = 2*Math.PI*(1/((double)sampleRate/(double)tone.get()));
        int fadeFrames = 32;
        double workingVolume = Short.MAX_VALUE * volume.get();
        double volumeStep = workingVolume/fadeFrames;
        double tVolume = 0;


        //Genereate a sine wave for the number of samples predicted, raise the volume up to target volume for MARK, and reduce volume to zero for space
        //controlled by fade frames, this keeps the audio from clicking
        for (int i = 0; i < numSamples; i++) {
            double angle = i*pre;
            if (thisElement.type == CodeParticle.Type.SPACE || i > (numSamples-fadeFrames)) {
                tVolume -= volumeStep;
            } else {
                tVolume += volumeStep;
            }
            if(tVolume < 0.01) {
                tVolume = 0;
            } else if(tVolume > workingVolume) {
                tVolume = workingVolume;
            }
            bb.putShort((short)(Math.sin(pre*i)*tVolume));
        }
    }

    private void playSample(AudioInputStream ais) {
        int nBytesRead = 0;
        byte[] abData = new byte[(int)ais.getFormat().getSampleRate()];
        while (nBytesRead != -1) {
            try {
                nBytesRead = ais.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = line.write(abData, 0, nBytesRead);
            }
        }
    }

    private void playCodeList(Playable myJob,ByteBuffer bb) {
        var data = Codec.translateString(myJob.getMessage());
        for(CodeCharacter cc : data) {

            //if(playPhonetic.get()) { playPhonetic(cc); }
            for(CodeParticle particle : cc.particles) {
                if(cancelPlayback) { break; }
                insertCodeElement(particle,bb);
                line.write(bb.array(),0,bb.position());
                bb.rewind();
            }

            Platform.runLater(() -> myJob.getReporter().accept(cc));
            if(cancelPlayback) { break; }
        }
    }

    private void evalOscillatorMode(ByteBuffer bb) {
        //double interval = (1.0/sampleRate)

        //long lastSample = 0;
        int i=0;
        while(oscOnFunct.test(null)) {
            //long cTime = System.currentTimeMillis();
            //if( (cTime - lastSample ) > interval) {
                double angle = 2.0 * Math.PI * i / ((double) sampleRate / (double) tone.get());
                double out = (Math.sin(angle) * volume.get());
                bb.putShort((short) out);
                line.write(bb.array(), 0, bb.limit());
                bb.rewind();
                i++;
              //  lastSample = cTime;
            }
    }

    @Override
    public void run() {

        //buffer up to 1 second of audio
        ByteBuffer bb = ByteBuffer.allocate(sampleRate*1*2); //Allocate buffer up here to keep it from re-allocating in loop
        bb.order(ByteOrder.LITTLE_ENDIAN);
        try {
            line = AudioSystem.getSourceDataLine(defaultAudioFormat);
            line.open(defaultAudioFormat);
            line.start();
            Platform.runLater(() -> isRunning.set(true));

        } catch (LineUnavailableException e) {
            quit = true;
        }

        while (!quit) {
            try {
                if (oscillatorMode.get()) {

                    evalOscillatorMode(bb);

                } else {

                    if (queue.isEmpty()) {
                        cancelPlayback = false;
                        continue;
                    }

                    Playable myJob = queue.take();
                    switch (myJob.type) {
                        case CODE -> playCodeList(myJob, bb);
                        case SAMPLES -> playSample(myJob.getSampleData());
                    }
                    Platform.runLater(myJob.getOnFinished());


                } // while(!quit)
                Platform.runLater(() -> isRunning.set(false));

            } catch (InterruptedException e) {
                quit = true;
            }
        }
        line.stop();
    }
}
