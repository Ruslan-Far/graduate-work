package com.ruslan.keyboard.repos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ruslan.keyboard.DatabaseHelper;
import com.ruslan.keyboard.entities.IMESettings;

public class IMESettingsRepo extends Repo {

    public IMESettingsRepo(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public IMESettings select() {
        IMESettings imeSettings;
        String[] columns;
        Cursor cursor;

        imeSettings = null;
        columns = new String[] {
                DatabaseHelper.IME_SETTINGS_COLUMN_ID, DatabaseHelper.IME_SETTINGS_COLUMN_SOUND,
                DatabaseHelper.IME_SETTINGS_COLUMN_VIBRATION, DatabaseHelper.IME_SETTINGS_COLUMN_CANDIDATES
        };
        cursor = database.query(DatabaseHelper.IME_SETTINGS_TABLE, columns,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            imeSettings = new IMESettings(
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IME_SETTINGS_COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IME_SETTINGS_COLUMN_SOUND)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IME_SETTINGS_COLUMN_VIBRATION)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.IME_SETTINGS_COLUMN_CANDIDATES))
            );
        }
        cursor.close();
        return imeSettings;
    }

    public long insert(IMESettings imeSettings) {
        ContentValues cv;

        cv = new ContentValues();
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_ID, imeSettings.getId());
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_SOUND, imeSettings.getSound());
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_VIBRATION, imeSettings.getVibration());
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_CANDIDATES, imeSettings.getCandidates());
        return database.insert(DatabaseHelper.IME_SETTINGS_TABLE, null, cv);
    }

    public long update(Integer id, IMESettings imeSettings) {
        ContentValues cv;

        cv = new ContentValues();
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_SOUND, imeSettings.getSound());
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_VIBRATION, imeSettings.getVibration());
        cv.put(DatabaseHelper.IME_SETTINGS_COLUMN_CANDIDATES, imeSettings.getCandidates());
        return database.update(DatabaseHelper.IME_SETTINGS_TABLE, cv,
                DatabaseHelper.IME_SETTINGS_COLUMN_ID + "=" + id, null);
    }
}
