package com.ruslan.keyboard.repos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ruslan.keyboard.DatabaseHelper;

//public class WordRepo implements IRestRepo<Word> {
//
//    private
//    public Word[] select() {
//
//    }
//
//    public Word select() {
//
//    }
//}
public class WordRepo {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public WordRepo(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public WordRepo open() {
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        database.close();
        databaseHelper.close();//
    }
}
