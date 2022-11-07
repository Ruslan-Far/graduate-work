package com.ruslan.keyboard.stores;

import com.ruslan.keyboard.entities.Word;

import java.util.List;

public class WordStore {

    public static List<Word> words;

    public static void postWord(Word word) {
        words.add(word);
    }

    public static void putWord(Integer id, Word word) {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getId() == id) {
                words.get(i).setCount(word.getCount());
                break;
            }
        }
    }
}
