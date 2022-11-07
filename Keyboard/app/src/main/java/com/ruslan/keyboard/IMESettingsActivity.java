package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class IMESettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ime_settings);
        setTitle(R.string.ime_settings_activity);
        Intent intent = new Intent(this, IME.class);
        startService(intent);
    }
}
