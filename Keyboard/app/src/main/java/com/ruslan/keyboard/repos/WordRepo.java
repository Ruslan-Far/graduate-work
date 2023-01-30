package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.R;
import com.ruslan.keyboard.entities.User;
import com.ruslan.keyboard.entities.Word;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WordRepo extends Repo {

    private Context mContext;

    public WordRepo(Context context) {
        super(context);
        mContext = context;
    }

    @SuppressLint("Range")
    public List<Word> select() {
        List<Word> words = new ArrayList<>();
        String[] columns = new String[] { DatabaseHelper.WORDS_COLUMN_ID,
                DatabaseHelper.WORDS_COLUMN_WORD, DatabaseHelper.WORDS_COLUMN_COUNT };
        Cursor cursor = database.query(DatabaseHelper.WORDS_TABLE, columns,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            words.add(new Word(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_WORD)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_COUNT))
            ));
        }
        cursor.close();
        if (words.size() == 0)
            return null;
        return words;
    }

    @SuppressLint("Range")
    public Word select(Integer id) {
        Word word = null;
        String[] columns = new String[] { DatabaseHelper.WORDS_COLUMN_ID,
                DatabaseHelper.WORDS_COLUMN_WORD, DatabaseHelper.WORDS_COLUMN_COUNT };
        Cursor cursor = database.query(DatabaseHelper.WORDS_TABLE, columns,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            word = new Word(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_WORD)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WORDS_COLUMN_COUNT))
            );
        }
        cursor.close();
        return word;
    }

    public long insert(Word word) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.WORDS_COLUMN_WORD, word.getWord());
        cv.put(DatabaseHelper.WORDS_COLUMN_COUNT, word.getCount());
        return database.insert(DatabaseHelper.WORDS_TABLE, null, cv);
    }

    public void insertAll() {
        String words;
        String[] arrWords;
        ContentValues cv;

        words = readWords();
        if (words.length() == 0)
            return;
        cv = new ContentValues();
        arrWords = words.split("\n");
        for (int i = 0; i < arrWords.length; i++) {
            cv.put(DatabaseHelper.WORDS_COLUMN_WORD, arrWords[i]);
            cv.put(DatabaseHelper.WORDS_COLUMN_COUNT, Constants.NEEDED_MAX_WORDS_COUNT);
            database.insert(DatabaseHelper.WORDS_TABLE, null, cv);
        }
    }

    private String readWords() {
        InputStream is = null;
        String words = "";

        try {
            is = mContext.getResources().openRawResource(R.raw.words);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            words = new String(bytes);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (is != null)
                    is.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return words;
    }

    public long update(Integer id, Word word) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.WORDS_COLUMN_COUNT, word.getCount());
        return database.update(DatabaseHelper.WORDS_TABLE, cv,
                DatabaseHelper.WORDS_COLUMN_ID + "=" + id, null);
    }
}
