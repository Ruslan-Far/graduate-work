package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.R;
import com.ruslan.keyboard.entities.WordFromDictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DictionaryRepo extends Repo {

    private Context mContext;

    public DictionaryRepo(Context context) {
        super(context);
        mContext = context;
    }

    @SuppressLint("Range")
    public List<WordFromDictionary> select() {
        List<WordFromDictionary> dictionary = new ArrayList<>();
        String[] columns = new String[] {DatabaseHelper.DICTIONARY_COLUMN_ID, DatabaseHelper.DICTIONARY_COLUMN_WORD};
        Cursor cursor = database.query(DatabaseHelper.DICTIONARY_TABLE, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            dictionary.add(new WordFromDictionary(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DICTIONARY_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.DICTIONARY_COLUMN_WORD))
            ));
        }
        cursor.close();
        if (dictionary.size() == 0)
            return null;
        return dictionary;
    }

    public void insert() {
        String dictionary;
        String[] arrDictionary;
        ContentValues cv;

        dictionary = readDictionary();
        if (dictionary.length() == 0)
            return;
        cv = new ContentValues();
        arrDictionary = dictionary.split("\n");
        for (int i = 0; i < arrDictionary.length; i++) {
            cv.put(DatabaseHelper.DICTIONARY_COLUMN_WORD, arrDictionary[i]);
            database.insert(DatabaseHelper.DICTIONARY_TABLE, null, cv);
        }
    }

    private String readDictionary() {
        InputStream is = null;
        String dictionary = "";

        try {
            is = mContext.getResources().openRawResource(R.raw.dictionary);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            dictionary = new String(bytes);
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
        return dictionary;
    }
}
