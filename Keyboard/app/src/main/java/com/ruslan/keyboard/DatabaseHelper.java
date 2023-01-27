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

    public static final String DICTIONARY_TABLE = "dictionary";
    public static final String DICTIONARY_COLUMN_ID = "id";
    public static final String DICTIONARY_COLUMN_WORD = "word";

    private String createUserQuery =
            "CREATE TABLE " + USER_TABLE +
                "(" +
                    USER_COLUMN_ID + " SERIAL PRIMARY KEY," +
                    USER_COLUMN_LOGIN + " TEXT DEFAULT '' NOT NULL" +
                ")";

    private String createDictionaryQuery =
            "CREATE TABLE " + DICTIONARY_TABLE +
                "(" +
                    DICTIONARY_COLUMN_ID + " SERIAL PRIMARY KEY," +
                    DICTIONARY_COLUMN_WORD + " TEXT DEFAULT '' NOT NULL" +
                ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserQuery);
        db.execSQL(createDictionaryQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
