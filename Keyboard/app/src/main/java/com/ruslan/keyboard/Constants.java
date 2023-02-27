package com.ruslan.keyboard;

public class Constants {

    public static final String API_BASE_URL = "http://192.168.1.100:8080/";
//    public static final String API_BASE_URL = "http://192.168.0.60:8080/";
//    public static final String API_BASE_URL = "http://192.168.1.102:8080/";
//    public static final String API_BASE_URL = "http://192.168.1.101:8080/";
//    public static final String API_BASE_URL = "http://192.168.161.71:8080/";

    public static final int NEEDED_MAX_WORDS_COUNT = 3;
    public static final int NUMBER_OF_HINTS = 3;
    public static final String EXPAND = "expand";
    public static final String ERROR_TRANSFER_DATA = "Ошибка в передачи данных. Повторите попытку позже";
    public static final String ERROR_CONNECTION = "Проблемы с соединением. Повторите попытку позже";
    public static final String EMPTY_SYM = "";
    public static final int ADDIT_LING_SERV_NUM = 0;
    public static final int ORTHO_LING_SERV_NUM = 1;
    public static final int PRED_LING_SERV_NUM = 2;
    public static final String MARK = "0";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final int SOUND = 0;
    public static final int VIBRATION = 1;
    public static final int CANDIDATES = 2;

    public static class KeyCode {

        public static final int SPACE = 32;
        public static final int RETURN = 10;
    }

    public enum KEYS_TYPE {
        SYMBOLS, ENGLISH, RUSSIAN
    }
}
