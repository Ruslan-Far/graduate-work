package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.entities.Collocation;

import java.util.ArrayList;
import java.util.List;

public class CollocationRepo extends Repo {

    public CollocationRepo(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<Collocation> select() {
        List<Collocation> collocations = new ArrayList<>();
        String[] columns = new String[] { DatabaseHelper.COLLOCATIONS_COLUMN_ID,
                DatabaseHelper.COLLOCATIONS_COLUMN_PREV_ID, DatabaseHelper.COLLOCATIONS_COLUMN_NEXT_ID,
                DatabaseHelper.COLLOCATIONS_COLUMN_COUNT };
        Cursor cursor = database.query(DatabaseHelper.COLLOCATIONS_TABLE, columns,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            collocations.add(new Collocation(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLLOCATIONS_COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLLOCATIONS_COLUMN_PREV_ID)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLLOCATIONS_COLUMN_NEXT_ID)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLLOCATIONS_COLUMN_COUNT))
            ));
        }
        cursor.close();
        if (collocations.size() == 0)
            return null;
        return collocations;
    }

    public long insert(Collocation collocation) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLLOCATIONS_COLUMN_PREV_ID, collocation.getPrevId());
        cv.put(DatabaseHelper.COLLOCATIONS_COLUMN_NEXT_ID, collocation.getNextId());
        cv.put(DatabaseHelper.COLLOCATIONS_COLUMN_COUNT, collocation.getCount());
        return database.insert(DatabaseHelper.COLLOCATIONS_TABLE, null, cv);
    }

    public long update(Integer id, Collocation collocation) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLLOCATIONS_COLUMN_COUNT, collocation.getCount());
        return database.update(DatabaseHelper.COLLOCATIONS_TABLE, cv,
                DatabaseHelper.COLLOCATIONS_COLUMN_ID + "=" + id, null);
    }

    public void delete() {
        ContentValues cv;

        database.delete(DatabaseHelper.COLLOCATIONS_TABLE, null, null);
        cv = new ContentValues();
        cv.put("seq", 0);
        database.update("sqlite_sequence", cv, "name=" + DatabaseHelper.COLLOCATIONS_TABLE, null);
    }
}
