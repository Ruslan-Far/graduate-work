package com.ruslan.keyboard;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class LatinIMEBackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate() {
        addHelper("shared_pref", new SharedPreferencesBackupHelper(this,
                getPackageName() + "_preferences"));
    }
}
