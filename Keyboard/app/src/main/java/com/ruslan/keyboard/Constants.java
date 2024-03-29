package com.ruslan.keyboard;

public class Constants {

    public static final String API_BASE_URL = "http://192.168.236.71:8080/";

    public static final int NEEDED_MAX_WORDS_COUNT = 10;
    public static final int NUMBER_OF_HINTS = 3;
    public static final String EXPAND = "expand";
    public static final String ERROR_TRANSFER_DATA = "Ошибка в передачи данных. Повторите попытку позже";
    public static final String ERROR_CONNECTION = "Проблемы с соединением. Повторите попытку позже";
    public static final String EMPTY_SYM = "";
    public static final int ADDIT_LING_SERV_NUM = 0;
    public static final int ORTHO_LING_SERV_NUM = 1;
    public static final int PRED_LING_SERV_NUM = 2;
    public static final String MARK = "0";
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static class KeyCode {

        public static final int SPACE = 32;
        public static final int RETURN = 10;
    }

    public enum KEYS_TYPE {
        SYMBOLS, ENGLISH, RUSSIAN
    }
}
