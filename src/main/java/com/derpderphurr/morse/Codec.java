package com.derpderphurr.morse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Codec {

    //output chars . one unit - 3 units,on unit space between symbols, 3 units of space between letters, 7 units space between words
    public static final Map<Character,String> decodeMap;

    static {
        decodeMap = new HashMap<>();
        decodeMap.put('e',".");
        decodeMap.put('t',"-");
        decodeMap.put('i',"..");
        decodeMap.put('a',".-");
        decodeMap.put('n',"-.");
        decodeMap.put('m',"--");
        decodeMap.put('s',"...");
        decodeMap.put('u',"..-");
        decodeMap.put('r',".-.");
        decodeMap.put('w',".--");
        decodeMap.put('d',"-..");
        decodeMap.put('k',"-.-");
        decodeMap.put('g',"--.");
        decodeMap.put('o',"---");
        decodeMap.put('h',"....");
        decodeMap.put('v',"...-");
        decodeMap.put('f',"..-.");
        decodeMap.put('l',".-..");
        decodeMap.put('p',".--.");
        decodeMap.put('j',".---");
        decodeMap.put('b',"-...");
        decodeMap.put('x',"-..-");
        decodeMap.put('c',"-.-.");
        decodeMap.put('y',"-.--");
        decodeMap.put('z',"--..");
        decodeMap.put('q',"--.-");
        decodeMap.put('1',".----");
        decodeMap.put('2',"..---");
        decodeMap.put('3',"...--");
        decodeMap.put('4',"....-");
        decodeMap.put('5',".....");
        decodeMap.put('6',"-....");
        decodeMap.put('7',"--...");
        decodeMap.put('8',"---..");
        decodeMap.put('9',"----.");
        decodeMap.put('0',"-----");
        decodeMap.put(',',"--..--");
        decodeMap.put('?',"..--..");
        decodeMap.put('.',".-.-.-");
        decodeMap.put('_',"..--.-");
        decodeMap.put('-',"-....-");
        decodeMap.put(':',"---...");
        decodeMap.put('=',"-...-");
        decodeMap.put('\"',".-..-.");
        decodeMap.put('/',"-..-.");
        decodeMap.put('$',"...-..-");
        //Punctuation stolen from: https://www.electronics-notes.com/articles/ham_radio/morse_code/characters-table-chart.php
    }

    private static Stream<CodeElement> translateDitDah(char c) {
        if( c == '.' ) {
            return Stream.of(new CodeElement(CodeElement.Type.MARK,1));
        } else if( c == '-' ) {
            return Stream.of(new CodeElement(CodeElement.Type.MARK,3));
        } else {
            //any char other than . or - is considered a space
            return Stream.of(new CodeElement(CodeElement.Type.SPACE,4));
        }
    }

    private static Stream<CodeElement> translateCharacter(char ch) {
        String letter = decodeMap.getOrDefault(ch," ");
        return Stream.concat(
                letter.chars().sequential() //decode char into its .- pattern
                        .mapToObj(c -> translateDitDah((char)c)) // translate those chars into marks/space
                        .flatMap( ce ->
                            Stream.concat(ce,Stream.of(new CodeElement(CodeElement.Type.SPACE,1))//add an extra space of 1 unit time, between the symbols
                        )
                ),
                Stream.of(new CodeElement(CodeElement.Type.SPACE,2)) //add 2 additional units to the end of the last char, making the distance between char 3 uints long
                );
    }

    public static List<CodeElement> translateString(String string) {
        return string
                .toLowerCase(Locale.ROOT)   //everything to lowercase
                .replaceAll(" +"," ")   //replace multiple spaces with a single space
                .chars().sequential()   //turn into instream of char, keep everything in order
                .mapToObj(c -> translateCharacter((char)c)) //translate character into mark space stream
                .flatMap(Function.identity()) // mash all streams together into one stream
                .collect(Collectors.toList());  // return a list of the mark,space elements
    }


    public static int WPMTimeUnitToNumSamples(int wpm,int samplerate) {
        //wpm = 50 elements == 1 word, ~ (50 * wpm) / 60 / 1000
        return (int)(1/(((double)wpm*50.0)/60.0/(double)samplerate));
    }

    public static int timeUnitsToNumSamples(int timeUnits,int wpm, int sampleRate) {
        return WPMTimeUnitToNumSamples(wpm,sampleRate) * timeUnits;
    }

}
