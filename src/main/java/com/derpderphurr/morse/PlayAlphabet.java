package com.derpderphurr.morse;

import java.util.stream.Collectors;

public class PlayAlphabet extends Thread {

    private final PlayerThread player;

    public PlayAlphabet(PlayerThread player) {
        this.player = player;
    }

    @Override
    public void run() {
        var alphabet = Codec.phonetics.keySet().stream().sorted().collect(Collectors.toList());

        player.playPhoneticProperty().set(true);

        for(Character c : alphabet) {
            String sc = new String(""+c);
            player.queueMessage(new PlayJob(sc));
        }

    }
}
