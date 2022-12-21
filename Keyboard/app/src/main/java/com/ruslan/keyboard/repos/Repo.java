package com.ruslan.keyboard.repos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ruslan.keyboard.DatabaseHelper;

public class Repo {

    private DatabaseHelper databaseHelper;
    protected SQLiteDatabase database;

    protected Repo(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }
}
