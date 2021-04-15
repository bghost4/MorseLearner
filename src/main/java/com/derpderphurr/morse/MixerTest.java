package com.derpderphurr.morse;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MixerTest {

    public static void playString(String s,int toneFreq,int wpm,int volume) {

        double period = 44100.0/toneFreq;
        double amplitude = volume;

        List<CodeParticle> data = Codec.translateString(s);

        AudioFormat af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,true);
        try {
            SourceDataLine line = AudioSystem.getSourceDataLine(af);
            line.open(af);

            line.start();

            ByteBuffer bb = ByteBuffer.allocate(4);

            for(int index=0; index < data.size(); index++) {
                CodeParticle thisElement = data.get(index);

                int numSamples = Codec.timeUnitsToNumSamples(thisElement.units,wpm,44100);

                for (int i=0; i < numSamples ; i++) {
                    if(thisElement.type == CodeParticle.Type.SPACE) {
                        bb.putShort((short)0);
                        bb.putShort((short)0);
                        line.write(bb.array(),0,4);
                        bb.rewind();
                    } else {
                        double angle = 2.0 * Math.PI * i / period;
                        double out = (Math.sin(angle) * amplitude);
                        bb.putShort((short) out);
                        bb.putShort((short) out);
                        line.write(bb.array(), 0, 4);
                        bb.rewind();
                    }
                }
            }
            line.stop();
            line.close();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        //playString("A B C",600,15,4000);
        try {
            Codec.dumpWaveInfo("A",15,44100,new File("wavedump.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
