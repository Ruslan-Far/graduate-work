package com.ruslan.keyboard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.stores.IMESettingsStore;

public class CanThemeActivity extends AppCompatActivity {

    private DatabaseInteraction mDatabaseInteraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.can_theme_activity);
        setContentView(R.layout.activity_can_theme);
        mDatabaseInteraction = new DatabaseInteraction(this);
        mDatabaseInteraction.selectIMESettings();
        initBackgroundColor();
        initTextColor();
        initFont();
    }

    private void initBackgroundColor() {
        String[] values;
        Spinner spinner;
        ArrayAdapter<String> adapter;
        Integer backgroundColor;

        values = new String[]{ "Белый", "Черный" };
        spinner = findViewById(R.id.backgroundColorSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        backgroundColor = IMESettingsStore.imeSettings.getCanBackgroundColor();
        if (backgroundColor == getResources().getColor(R.color.white))
            spinner.setSelection(0);
        else if (backgroundColor == getResources().getColor(R.color.black))
            spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer backgroundColor;

                mDatabaseInteraction.selectIMESettings();
                if (position == 0)
                    backgroundColor = getResources().getColor(R.color.white);
                else
                    backgroundColor = getResources().getColor(R.color.black);
                if (IMESettingsStore.imeSettings.getCanBackgroundColor().equals(backgroundColor))
                    return;
                IMESettingsStore.imeSettings.setCanBackgroundColor(backgroundColor);
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initTextColor() {
        initAdditTextColor();
        initOrthoTextColor();
        initPredTextColor();
    }

    private void initAdditTextColor() {
        String[] values;
        Spinner spinner;
        ArrayAdapter<String> adapter;
        Integer additTextColor;

        values = new String[]{ "Темно-голубой", "Зеленый", "Синий" };
        spinner = findViewById(R.id.additTextColorSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        additTextColor = IMESettingsStore.imeSettings.getCanAdditTextColor();
        if (additTextColor == getResources().getColor(R.color.dark_cyan))
            spinner.setSelection(0);
        else if (additTextColor == getResources().getColor(R.color.green))
            spinner.setSelection(1);
        else if (additTextColor == getResources().getColor(R.color.dark_blue))
            spinner.setSelection(2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer additTextColor;

                mDatabaseInteraction.selectIMESettings();
                if (position == 0)
                    additTextColor = getResources().getColor(R.color.dark_cyan);
                else if (position == 1)
                    additTextColor = getResources().getColor(R.color.green);
                else
                    additTextColor = getResources().getColor(R.color.dark_blue);
                if (IMESettingsStore.imeSettings.getCanAdditTextColor().equals(additTextColor))
                    return;
                IMESettingsStore.imeSettings.setCanAdditTextColor(additTextColor);
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initOrthoTextColor() {
        String[] values;
        Spinner spinner;
        ArrayAdapter<String> adapter;
        Integer orthoTextColor;

        values = new String[]{ "Темно-голубой", "Зеленый", "Синий" };
        spinner = findViewById(R.id.orthoTextColorSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        orthoTextColor = IMESettingsStore.imeSettings.getCanOrthoTextColor();
        if (orthoTextColor == getResources().getColor(R.color.dark_cyan))
            spinner.setSelection(0);
        else if (orthoTextColor == getResources().getColor(R.color.green))
            spinner.setSelection(1);
        else if (orthoTextColor == getResources().getColor(R.color.dark_blue))
            spinner.setSelection(2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer orthoTextColor;

                mDatabaseInteraction.selectIMESettings();
                if (position == 0)
                    orthoTextColor = getResources().getColor(R.color.dark_cyan);
                else if (position == 1)
                    orthoTextColor = getResources().getColor(R.color.green);
                else
                    orthoTextColor = getResources().getColor(R.color.dark_blue);
                if (IMESettingsStore.imeSettings.getCanOrthoTextColor().equals(orthoTextColor))
                    return;
                IMESettingsStore.imeSettings.setCanOrthoTextColor(orthoTextColor);
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initPredTextColor() {
        String[] values;
        Spinner spinner;
        ArrayAdapter<String> adapter;
        Integer predTextColor;

        values = new String[]{ "Темно-голубой", "Зеленый", "Синий" };
        spinner = findViewById(R.id.predTextColorSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        predTextColor = IMESettingsStore.imeSettings.getCanPredTextColor();
        if (predTextColor == getResources().getColor(R.color.dark_cyan))
            spinner.setSelection(0);
        else if (predTextColor == getResources().getColor(R.color.green))
            spinner.setSelection(1);
        else if (predTextColor == getResources().getColor(R.color.dark_blue))
            spinner.setSelection(2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer predTextColor;

                mDatabaseInteraction.selectIMESettings();
                if (position == 0)
                    predTextColor = getResources().getColor(R.color.dark_cyan);
                else if (position == 1)
                    predTextColor = getResources().getColor(R.color.green);
                else
                    predTextColor = getResources().getColor(R.color.dark_blue);
                if (IMESettingsStore.imeSettings.getCanPredTextColor().equals(predTextColor))
                    return;
                IMESettingsStore.imeSettings.setCanPredTextColor(predTextColor);
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initFont() {
        String[] values;
        Spinner spinner;
        ArrayAdapter<String> adapter;
        String font;

        values = new String[]{ "monospace", "serif", "serif-monospace", "sans-serif", "sans-serif-condensed",
                                "sans-serif-smallcaps", "sans-serif-light", "casual", "cursive" };
        spinner = findViewById(R.id.fontSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        font = IMESettingsStore.imeSettings.getCanFont();
        for (int i = 0; i < values.length; i++) {
            if (font.equals(values[i])) {
                spinner.setSelection(i);
                break;
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String font;

                mDatabaseInteraction.selectIMESettings();
                font = values[position];
                if (IMESettingsStore.imeSettings.getCanFont().equals(font))
                    return;
                IMESettingsStore.imeSettings.setCanFont(font);
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
