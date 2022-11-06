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
    public static final String USER_COLUMN_PASSWORD = "password";

//    public static final String USERS_TABLE = "users";
//    public static final String USERS_COLUMN_ID = "id";
//    public static final String USERS_COLUMN_LOGIN = "login";
//    public static final String USERS_COLUMN_PASSWORD = "password";
//
//    public static final String WORDS_TABLE = "words";
//    public static final String WORDS_COLUMN_ID = "id";
//    public static final String WORDS_COLUMN_USERID = "userId";
//    public static final String WORDS_COLUMN_WORD = "word";
//    public static final String WORDS_COLUMN_COUNT = "count";
//
//    public static final String COLLOCATIONS_TABLE = "collocations";
//    public static final String COLLOCATIONS_COLUMN_ID = "id";
//    public static final String COLLOCATIONS_COLUMN_PREVID = "prevId";
//    public static final String COLLOCATIONS_COLUMN_NEXTID = "nextId";
//    public static final String COLLOCATIONS_COLUMN_COUNT = "count";

    private String createUserQuery =
            "CREATE TABLE " + USER_TABLE +
                "(" +
                    USER_COLUMN_ID + " SERIAL PRIMARY KEY," +
                    USER_COLUMN_LOGIN + " TEXT DEFAULT '' NOT NULL," +
                    USER_COLUMN_PASSWORD + " TEXT DEFAULT '' NOT NULL" +
                ")";

//    private String createUsersQuery =
//        "CREATE TABLE " + USERS_TABLE +
//        "(" +
//            USERS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//            USERS_COLUMN_LOGIN + " TEXT DEFAULT '' NOT NULL," +
//            USERS_COLUMN_PASSWORD + " TEXT DEFAULT '' NOT NULL" +
//        ")";
//    private String createWordsQuery =
//        "CREATE TABLE " + WORDS_TABLE +
//        "(" +
//            WORDS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//            WORDS_COLUMN_USERID + " INTEGER REFERENCES \"" + USERS_TABLE + "\" (\"" + USERS_COLUMN_ID + "\")," +
//            WORDS_COLUMN_WORD + " TEXT DEFAULT '' NOT NULL," +
//            WORDS_COLUMN_COUNT + " INTEGER NOT NULL" +
//        ")";
//    private String createCollocationsQuery =
//        "CREATE TABLE " + COLLOCATIONS_TABLE +
//        "(" +
//            COLLOCATIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//            COLLOCATIONS_COLUMN_PREVID + " INTEGER REFERENCES \"" + WORDS_TABLE + "\" (\"" + WORDS_COLUMN_ID + "\")," +
//            COLLOCATIONS_COLUMN_NEXTID + " INTEGER REFERENCES \"" + WORDS_TABLE + "\" (\"" + WORDS_COLUMN_ID + "\")," +
//            COLLOCATIONS_COLUMN_COUNT + " INTEGER NOT NULL" +
//        ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserQuery);
//        db.execSQL(createUsersQuery);
//        db.execSQL(createWordsQuery);
//        db.execSQL(createCollocationsQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + COLLOCATIONS_TABLE);
//        onCreate(db);
    }
}
