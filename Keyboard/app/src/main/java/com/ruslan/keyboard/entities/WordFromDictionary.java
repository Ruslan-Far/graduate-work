package com.ruslan.keyboard.entities;

public class WordFromDictionary extends BaseEntity {

    private String word;

    public WordFromDictionary() {}

    public WordFromDictionary(Integer id, String word) {
        super(id);
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
