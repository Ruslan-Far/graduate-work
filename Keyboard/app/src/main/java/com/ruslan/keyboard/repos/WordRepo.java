package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.entities.Word;

import java.util.ArrayList;

public class WordRepo {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public WordRepo(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    @SuppressLint("Range")
    public Word[] select() {
        ArrayList<Word> values = new ArrayList<>();
        String[] columns = new String[] { DatabaseHelper.WORDS_COLUMN_ID, DatabaseHelper.WORDS_COLUMN_USERID, DatabaseHelper.WORDS_COLUMN_WORD, DatabaseHelper.WORDS_COLUMN_COUNT };
        Cursor cursor = database.query(DatabaseHelper.WORDS_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            values.add(new Word(
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_USERID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_WORD)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_COUNT))
            ));
        }
        cursor.close();
        if (values.size() == 0)
            return null;
        Word[] words = new Word[values.size()];
        words = values.toArray(words);
        return words;
    }

//    public Word select(Integer id) {
//
//    }

    public long insert(Word word) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.WORDS_COLUMN_USERID, word.getUserId());
        cv.put(DatabaseHelper.WORDS_COLUMN_WORD, word.getWord());
        cv.put(DatabaseHelper.WORDS_COLUMN_COUNT, word.getCount());
        return database.insert(DatabaseHelper.WORDS_TABLE, null, cv);
    }
}
