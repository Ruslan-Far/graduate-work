package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class IMESettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, IME.class);
        startService(intent);
        setContentView(R.layout.activity_ime_settings);
        setTitle(R.string.ime_settings_activity);
        String[] imeSettings = new String[] { "Шрифт", "Тема" };
        ListView imeSettingsList = findViewById(R.id.imeSettingsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, imeSettings);
        imeSettingsList.setAdapter(adapter);
    }
}
