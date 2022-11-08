package com.ruslan.keyboard;

public class Alphabet {

    private static boolean isRussian(char ch) {
        return ch >= 'А' && ch <= 'я' || ch == 'Ё' || ch == 'ё';
    }

    private static boolean isEnglish(char ch) {
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z';
    }

    public static boolean isAlphabetChar(char ch) {
        return isRussian(ch) || isEnglish(ch);
    }
}
