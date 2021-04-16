package com.derpderphurr.morse;

import javafx.application.Platform;
import javafx.beans.property.*;

import javax.sound.sampled.*;
import javax.xml.transform.Source;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

public class PlayerThread extends Thread {

    private final SimpleBooleanProperty playing = new SimpleBooleanProperty(false);
    private final SimpleIntegerProperty wpm = new SimpleIntegerProperty(20);
    private final SimpleIntegerProperty tone = new SimpleIntegerProperty(660);
    private final SimpleDoubleProperty volume = new SimpleDoubleProperty(4096); //probably should rescale this to the bounds of a short
    private final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(false);

    public IntegerProperty wpmProperty() { return wpm; }
    public IntegerProperty toneProperty() { return tone; }
    public DoubleProperty volumeProperty() { return volume; }
    public ReadOnlyBooleanProperty isRunningProperty() { return isRunning; }
    public ReadOnlyBooleanProperty isPlayingProperty() { return playing; }

    private SourceDataLine line;
    private final int sampleRate = 44100;
    private final AudioFormat defaultAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 1, 2, sampleRate, false);

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
    private final ArrayBlockingQueue<Character> phonetic = new ArrayBlockingQueue<>(5);

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

    private void playPhonetic(char c) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        //Load Phonetic as Clip
        InputStream clipStream = this.getClass().getResourceAsStream("/phonetic/a.wav");
        if(clipStream == null) {
            System.err.println("unable to load reasource");
        }
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(clipStream));

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

    public void queuePhonetic(char c) {
        phonetic.add(c);
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

                playPhonetic('a');

                var data = Codec.translateString(myJob.getMessage());

                ByteBuffer bb = ByteBuffer.allocate(2); //Allocate buffer up here to keep it from re-allocating in loop
                bb.order(ByteOrder.LITTLE_ENDIAN);

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
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        } // while(!quit)
        Platform.runLater(() -> isRunning.set(false));
    }
}
