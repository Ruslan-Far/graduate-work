package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.entities.WordFromDictionary;

import java.util.ArrayList;
import java.util.List;

public class DictionaryRepo extends Repo {

    public DictionaryRepo(Context context) {
        super(context);
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
        return dictionary;
    }
}
