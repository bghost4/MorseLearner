package com.derpderphurr.morse;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayAlphabet extends Thread {

    private final PlayerThread player;

    public PlayAlphabet(PlayerThread player) {
        this.player = player;
    }

    @Override
    public void run() {
        var alphabet = Codec.phonetics.keySet().stream().sorted().collect(Collectors.toList());

        for(Character c : alphabet) {
            try {
                Optional<AudioInputStream> oais = Codec.getPhoneticInputStream(c);
                oais.ifPresent((ais) -> player.queueMessage(new Playable(ais)) );
            } catch (IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }

            player.queueMessage(new Playable(String.format("%c   %c   %c   %c",c,c,c,c)));
        }

    }
}
