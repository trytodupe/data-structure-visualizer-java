package com.trytodupe.utils;

public class CharFreqencyPair {

    private final Character character;
    private final Integer freqency;
    public CharFreqencyPair(Character character, Integer freqency) {
        this.character = character;
        this.freqency = freqency;
    }

    public Integer getFreqency () {
        return freqency;
    }

    public Character getCharacter () {
        return character;
    }
}
