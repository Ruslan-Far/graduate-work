package com.ruslan.keyboard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.stores.IMESettingsStore;

public class LearningRateActivity extends AppCompatActivity {

    private DatabaseInteraction mDatabaseInteraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.learning_rate_activity);
        setContentView(R.layout.activity_learning_rate);
        mDatabaseInteraction = new DatabaseInteraction(this);
        Integer[] rates = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (IMESettingsStore.imeSettings == null)
            mDatabaseInteraction.selectIMESettings();
        spinner.setSelection(IMESettingsStore.imeSettings.getLearningRate() - 1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer rate = (Integer) parent.getItemAtPosition(position);
                if (IMESettingsStore.imeSettings == null)
                    mDatabaseInteraction.selectIMESettings();
                if (IMESettingsStore.imeSettings.getLearningRate().equals(rate)) {
                    System.out.println("LearningRateActivity RETURN");
                    return;
                }
                System.out.println("LearningRateActivity NOT RETURN");
                IMESettingsStore.imeSettings.setLearningRate(rate);
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
}
