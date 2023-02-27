package com.ruslan.keyboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_data.db";
    private static final int SCHEMA = 1;

    public static final String USER_TABLE = "user";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_LOGIN = "login";

    public static final String WORDS_TABLE = "words";
    public static final String WORDS_COLUMN_ID = "id";
    public static final String WORDS_COLUMN_WORD = "word";
    public static final String WORDS_COLUMN_COUNT = "count";

    public static final String COLLOCATIONS_TABLE = "collocations";
    public static final String COLLOCATIONS_COLUMN_ID = "id";
    public static final String COLLOCATIONS_COLUMN_PREV_ID = "prev_id";
    public static final String COLLOCATIONS_COLUMN_NEXT_ID = "next_id";
    public static final String COLLOCATIONS_COLUMN_COUNT = "count";

    public static final String IME_SETTINGS_TABLE = "ime_settings";
    public static final String IME_SETTINGS_COLUMN_ID = "id";
    public static final String IME_SETTINGS_COLUMN_SOUND = "sound";
    public static final String IME_SETTINGS_COLUMN_VIBRATION = "vibration";
    public static final String IME_SETTINGS_COLUMN_CANDIDATES = "candidates";

    private String createUserQuery =
            "CREATE TABLE " + USER_TABLE +
                "(" +
                    USER_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    USER_COLUMN_LOGIN + " TEXT DEFAULT '' NOT NULL" +
                ")";

    private String createWordsQuery =
            "CREATE TABLE " + WORDS_TABLE +
                "(" +
                    WORDS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WORDS_COLUMN_WORD + " TEXT DEFAULT '' NOT NULL," +
                    WORDS_COLUMN_COUNT + " INTEGER NOT NULL" +
                ")";

    private String createCollocationsQuery =
            "CREATE TABLE " + COLLOCATIONS_TABLE +
                "(" +
                    COLLOCATIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLLOCATIONS_COLUMN_PREV_ID + " INTEGER REFERENCES \"words\" (\"id\")," +
                    COLLOCATIONS_COLUMN_NEXT_ID + " INTEGER REFERENCES \"words\" (\"id\")," +
                    COLLOCATIONS_COLUMN_COUNT + " INTEGER NOT NULL" +
                ")";

    private String createIMESettingsQuery =
            "CREATE TABLE " + IME_SETTINGS_TABLE +
                "(" +
                    IME_SETTINGS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    IME_SETTINGS_COLUMN_SOUND + " INTEGER NOT NULL," +
                    IME_SETTINGS_COLUMN_VIBRATION + " INTEGER NOT NULL," +
                    IME_SETTINGS_COLUMN_CANDIDATES + " INTEGER NOT NULL" +
                ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserQuery);
        db.execSQL(createWordsQuery);
        db.execSQL(createCollocationsQuery);
        db.execSQL(createIMESettingsQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
