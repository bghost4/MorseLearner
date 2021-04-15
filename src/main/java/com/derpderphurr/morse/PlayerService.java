package com.derpderphurr.morse;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

public class PlayerService extends Service<Void> {


    private SourceDataLine line;
    private int samplerate = 44100;
    private final SimpleIntegerProperty toneFreq = new SimpleIntegerProperty(800);
    private final SimpleIntegerProperty wpm = new SimpleIntegerProperty(15);
    private final SimpleIntegerProperty volume = new SimpleIntegerProperty(4000);

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(6);

    public int getToneFreq() {
        return toneFreq.get();
    }

    public SimpleIntegerProperty toneFreqProperty() {
        return toneFreq;
    }

    public int getWpm() {
        return wpm.get();
    }

    public SimpleIntegerProperty wpmProperty() {
        return wpm;
    }

    public int getVolume() {
        return volume.get();
    }

    public SimpleIntegerProperty volumeProperty() {
        return volume;
    }

    private boolean quit = false;

    public PlayerService() {
        super();

        this.executorProperty().setValue(Executors.newSingleThreadExecutor());

        AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, samplerate, 16, 1, 2, samplerate, true);
        try {
            line = AudioSystem.getSourceDataLine(af);
            line.open(af);
            line.start();
            System.out.println("Line Buffer Size: " + line.getBufferSize());
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public synchronized void playString(String s) {
        System.out.println("Adding " + s + " to Queue");

        if (!isRunning()) {
            System.err.println("Player Service is not running");
        }
        if (!queue.add(s)) {
            System.err.println("Queue Full");
        } else {
            System.out.println("Added to Queue");
        }
    }

    public void setToneFreq(int freq) {
        toneFreq.set(freq);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                System.err.println("Task Started");

                while (!quit) {
                    if (queue.isEmpty()) {
                        Thread.sleep(100);
                    } else {
                        double period = (double) samplerate / (double) toneFreq.get();
                        double amplitude = volume.get();

                        String myString = queue.remove();
                        System.out.println("Player Recv Data: " + myString);
                        List<CodeElement> data = Codec.translateString(myString);

                        ByteBuffer bb = ByteBuffer.allocate(2); //Allocate buffer up here to keep it from re-allocating in loop

                        for (int index = 0; index < data.size(); index++) {
                            CodeElement thisElement = data.get(index);
                            int numSamples = Codec.timeUnitsToNumSamples(thisElement.units, wpm.get(), samplerate);

                            for (int i = 0; i < numSamples; i++) {
                                if (thisElement.type == CodeElement.Type.SPACE) {
                                    bb.putShort((short) 0);
                                } else {
                                    double angle = 2.0 * Math.PI * i / period;
                                    double out = (Math.sin(angle) * amplitude);
                                    bb.putShort((short) out);
                                }
                                line.write(bb.array(), 0, bb.limit());
                                bb.rewind();
                            } //end of playing this element
                        } //end of for elements
                    } //end of else
                } // while(!quit)
                return null;
            } //end of Void call()
        }; //end of new Task
    } //end of CreateTask

    public void shutdown() {
        quit = true;
    }
}
