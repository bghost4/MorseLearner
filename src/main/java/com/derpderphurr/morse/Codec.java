package com.derpderphurr.morse;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Codec {

    //output chars . one unit - 3 units,on unit space between symbols, 3 units of space between letters, 7 units space between words
    public static final Map<String,String> decodeMap;

    static {
        decodeMap = new HashMap<>();
        decodeMap.put("e",".");
        decodeMap.put("t","-");
        decodeMap.put("i","..");
        decodeMap.put("a",".-");
        decodeMap.put("n","-.");
        decodeMap.put("m","--");
        decodeMap.put("s","...");
        decodeMap.put("u","..-");
        decodeMap.put("r",".-.");
        decodeMap.put("w",".--");
        decodeMap.put("d","-..");
        decodeMap.put("k","-.-");
        decodeMap.put("g","--.");
        decodeMap.put("o","---");
        decodeMap.put("h","....");
        decodeMap.put("v","...-");
        decodeMap.put("f","..-.");
        decodeMap.put("l",".-..");
        decodeMap.put("p",".--.");
        decodeMap.put("j",".---");
        decodeMap.put("b","-...");
        decodeMap.put("x","-..-");
        decodeMap.put("c","-.-.");
        decodeMap.put("y","-.--");
        decodeMap.put("z","--..");
        decodeMap.put("q","--.-");
        decodeMap.put("1",".----");
        decodeMap.put("2","..---");
        decodeMap.put("3","...--");
        decodeMap.put("4","....-");
        decodeMap.put("5",".....");
        decodeMap.put("6","-....");
        decodeMap.put("7","--...");
        decodeMap.put("8","---..");
        decodeMap.put("9","----.");
        decodeMap.put("0","-----");
        decodeMap.put(",","--..--");
        decodeMap.put("?","..--..");
        decodeMap.put(".",".-.-.-");
        decodeMap.put("_","..--.-");
        decodeMap.put("-","-....-");
        decodeMap.put(":","---...");
        decodeMap.put("=","-...-");
        decodeMap.put("\"",".-..-.");
        decodeMap.put("/","-..-.");
        decodeMap.put("$","...-..-");
        //Punctuation stolen from: https://www.electronics-notes.com/articles/ham_radio/morse_code/characters-table-chart.php
    }

    public static final Map<Character,String> phonetics = new HashMap<>();
    static {
        phonetics.put('a',"/phonetic/a.wav");
        phonetics.put('b',"/phonetic/b.wav");
        phonetics.put('c',"/phonetic/c.wav");
        phonetics.put('d',"/phonetic/d.wav");
        phonetics.put('e',"/phonetic/e.wav");
        phonetics.put('f',"/phonetic/f.wav");
        phonetics.put('g',"/phonetic/g.wav");
        phonetics.put('h',"/phonetic/h.wav");
        phonetics.put('i',"/phonetic/i.wav");
        phonetics.put('j',"/phonetic/j.wav");
        phonetics.put('k',"/phonetic/k.wav");
        phonetics.put('l',"/phonetic/l.wav");
        phonetics.put('m',"/phonetic/m.wav");
        phonetics.put('n',"/phonetic/n.wav");
        phonetics.put('o',"/phonetic/o.wav");
        phonetics.put('p',"/phonetic/p.wav");
        phonetics.put('q',"/phonetic/q.wav");
        phonetics.put('r',"/phonetic/r.wav");
        phonetics.put('s',"/phonetic/s.wav");
        phonetics.put('t',"/phonetic/t.wav");
        phonetics.put('u',"/phonetic/u.wav");
        phonetics.put('v',"/phonetic/v.wav");
        phonetics.put('w',"/phonetic/w.wav");
        phonetics.put('x',"/phonetic/x.wav");
        phonetics.put('y',"/phonetic/y.wav");
        phonetics.put('z',"/phonetic/z.wav");
    }

    public static Optional<AudioInputStream> getPhoneticInputStream(Character c) throws IOException, UnsupportedAudioFileException {
        if(phonetics.containsKey(c)) {
            return Optional.of(AudioSystem.getAudioInputStream(new BufferedInputStream(Codec.class.getResourceAsStream(phonetics.get(c)))));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<AudioInputStream> getPhoneticInputStream(CodeCharacter cc) throws IOException, UnsupportedAudioFileException {
        if (cc.getCha().length() == 1) {
            if (phonetics.containsKey(cc.cha.toCharArray()[0])) {
                InputStream clipStream = Codec.class.getResourceAsStream(phonetics.get(cc.cha.toCharArray()[0]));
                if(clipStream != null) {
                    return Optional.of(AudioSystem.getAudioInputStream(new BufferedInputStream(clipStream)));
                }
            }
        }
        return Optional.empty();
    }

    private static Stream<CodeParticle> translateDitDah(char c) {
        if( c == '.' ) {
            return Stream.of(new CodeParticle(CodeParticle.Type.MARK,1));
        } else if( c == '-' ) {
            return Stream.of(new CodeParticle(CodeParticle.Type.MARK,3));
        } else {
            //any char other than . or - is considered a space
            //System.err.println("Unknown code char: \"%c\"%n",c);
            return Stream.of(new CodeParticle(CodeParticle.Type.SPACE,4));
        }
    }

    private static CodeCharacter translateCharacter(String ch) {
        String letter = decodeMap.getOrDefault(ch, " ");

        return new CodeCharacter(ch,
                Stream.concat(
                        letter.chars().sequential() //decode char into its .- pattern
                                .mapToObj(c -> translateDitDah((char) c)) // translate those chars into marks/space
                                .flatMap(ce ->
                                        Stream.concat(ce, Stream.of(new CodeParticle(CodeParticle.Type.SPACE, 1))//add an extra space of 1 unit time, between the symbols
                                        )
                                ),
                        Stream.of(new CodeParticle(CodeParticle.Type.SPACE, 2)) //add 2 additional units to the end of the last char, making the distance between char 3 units long
                ).collect(Collectors.toList())
        );
    }

    private static Stream<String> fragment(String in) {
        //TODO add additional functionality to recognize codes that get grouped together
        return in.chars().mapToObj(c -> ""+(char)c);
    }

    public static List<CodeCharacter> translateString(String string) {
        return fragment(string
                .toLowerCase(Locale.ROOT)   //everything to lowercase
                .replaceAll(" +"," ")
                )   //replace multiple spaces with a single space
                .map(Codec::translateCharacter) //translate character into mark space stream
                .collect(Collectors.toList());  // return a list of the mark,space elements
    }

    public static int WPMToMsPerTimeUnit(int wpm) {
        return 1000/(wpm*50/60);
    }

    public static int WPMTimeUnitToNumSamples(int wpm,int sampleRate) {
        return (int)(1/(((double)wpm*50.0)/60.0/(double)sampleRate));
    }

    public static int timeUnitsToNumSamples(int timeUnits,int wpm, int sampleRate) {
        return WPMTimeUnitToNumSamples(wpm,sampleRate) * timeUnits;
    }

}
