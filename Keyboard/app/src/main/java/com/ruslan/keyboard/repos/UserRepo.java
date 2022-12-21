package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.entities.User;

public class UserRepo extends Repo {

    public UserRepo(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public User select() {
        User user = null;
        String[] columns = new String[] { DatabaseHelper.USER_COLUMN_ID, DatabaseHelper.USER_COLUMN_LOGIN, DatabaseHelper.USER_COLUMN_PASSWORD };
        Cursor cursor = database.query(DatabaseHelper.USER_TABLE, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            user = new User(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.USER_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_COLUMN_LOGIN)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_COLUMN_PASSWORD))
            );
        }
        cursor.close();
        return user;
    }

    public long insert(User user) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.USER_COLUMN_ID, user.getId());
        cv.put(DatabaseHelper.USER_COLUMN_LOGIN, user.getLogin());
        cv.put(DatabaseHelper.USER_COLUMN_PASSWORD, user.getPassword());
        return database.insert(DatabaseHelper.USER_TABLE, null, cv);
    }

    public long delete() {
        return database.delete(DatabaseHelper.USER_TABLE, null, null);
    }
}
