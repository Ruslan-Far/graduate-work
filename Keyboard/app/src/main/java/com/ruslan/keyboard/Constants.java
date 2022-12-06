package com.ruslan.keyboard;

public class Constants {

    public static final String API_BASE_URL = "http://192.168.1.101:8080/";
//     public static final String API_BASE_URL = "http://192.168.161.71:8080/";

    public static final int LIMIT_MAX_WORDS_COUNT = 3;
    public static final int NUMBER_OF_HINTS = 3;
    public static final String EXPAND = "expand";

    public static class KeyCode {

        public static final int SPACE = 32;

        public static final int RETURN = 10;
    }

    public enum KEYS_TYPE {
        SYMBOLS, ENGLISH, RUSSIAN
    }
}
