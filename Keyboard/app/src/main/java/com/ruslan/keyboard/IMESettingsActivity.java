package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.entities.IMESettings;
import com.ruslan.keyboard.stores.IMESettingsStore;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

public class IMESettingsActivity extends AppCompatActivity {

    private final static String TAG = "IMESettingsActivity";

    private DatabaseInteraction mDatabaseInteraction;

    public static String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Intent intent = new Intent(this, IME.class);
        startService(intent);

        setTitle(R.string.ime_settings_activity);
        mDatabaseInteraction = new DatabaseInteraction(this);
        mDatabaseInteraction.selectIMESettings();
        if (IMESettingsStore.imeSettings == null) {
            mDatabaseInteraction.insertIMESettings(new IMESettings());
            mDatabaseInteraction.selectIMESettings();
        }
        mDatabaseInteraction.selectWords();
        if (WordStore.words == null) {
            mDatabaseInteraction.insertWords();
            mDatabaseInteraction.selectWords();
        }
        errorMessage = Constants.EMPTY_SYM;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        UserStore.user = null;
        WordStore.words = null;
        IMESettingsStore.imeSettings = null;
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mDatabaseInteraction.selectUser();
        if (UserStore.user != null) {
            setContentView(R.layout.activity_ime_settings_user);
            TextView userLogin = findViewById(R.id.userLogin);
            userLogin.setText(UserStore.user.getLogin());
            Button exitButton = findViewById(R.id.exitButton);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseInteraction.deleteUser();
                    Intent intentActivity = new Intent(IMESettingsActivity.this, IMESettingsActivity.class);
                    intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentActivity);
                }
            });
        }
        else {
            setContentView(R.layout.activity_ime_settings);
            Button authButton = findViewById(R.id.authButton);
            authButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentActivity = new Intent(IMESettingsActivity.this, AuthActivity.class);
                    startActivity(intentActivity);
                }
            });
        }
        mDatabaseInteraction.selectIMESettings();
        ListView imeSettingsCheckboxList = findViewById(R.id.imeSettingsCheckboxList);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                    getResources().getStringArray(R.array.ime_settings_checkbox));
        imeSettingsCheckboxList.setAdapter(adapter);
        imeSettingsCheckboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem;

                if (IMESettingsStore.imeSettings == null)
                    mDatabaseInteraction.selectIMESettings();
                selectedItem = adapter.getItem(position);
                if (imeSettingsCheckboxList.isItemChecked(position)) {
                    if (selectedItem.equals(getString(R.string.sound_checkbox))) {
                        System.out.println("Установить звук");
                        IMESettingsStore.imeSettings.setSound(Constants.TRUE);
                    }
                    else if (selectedItem.equals(getString(R.string.vibration_checkbox))) {
                        System.out.println("Установить вибрацию");
                        IMESettingsStore.imeSettings.setVibration(Constants.TRUE);
                    }
                    else {
                        System.out.println("Установить подсказки");
                        IMESettingsStore.imeSettings.setCandidates(Constants.TRUE);
                    }
                }
                else {
                    if (selectedItem.equals(getString(R.string.sound_checkbox))) {
                        System.out.println("Отключить звук");
                        IMESettingsStore.imeSettings.setSound(Constants.FALSE);
                    }
                    else if (selectedItem.equals(getString(R.string.vibration_checkbox))) {
                        System.out.println("Отключить вибрацию");
                        IMESettingsStore.imeSettings.setVibration(Constants.FALSE);
                    }
                    else {
                        System.out.println("Отключить подсказки");
                        IMESettingsStore.imeSettings.setCandidates(Constants.FALSE);
                    }
                }
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
        });
        initIMESettingsCheckboxList(imeSettingsCheckboxList);
        ListView imeSettingsList = findViewById(R.id.imeSettingsList);
        imeSettingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentActivity = null;
                String selectedItem = (String) parent.getItemAtPosition(position);
                if (selectedItem.equals(getString(R.string.synchronization_activity))) {
                    System.out.println(getString(R.string.synchronization_activity));
                    intentActivity = new Intent(IMESettingsActivity.this, SynActivity.class);
                }
                else if (selectedItem.equals(getString(R.string.font_activity))) {
                    System.out.println(getString(R.string.font_activity));
                }
                else {
                    System.out.println(getString(R.string.theme_activity));
                }
                startActivity(intentActivity);
            }
        });
        if (errorMessage.length() != 0) {
            showErrorDialog();
        }
    }

    private void initIMESettingsCheckboxList(ListView imeSettingsCheckboxList) {
        if (IMESettingsStore.imeSettings.getSound() == Constants.TRUE)
            imeSettingsCheckboxList.setItemChecked(0, true);
        if (IMESettingsStore.imeSettings.getVibration() == Constants.TRUE)
            imeSettingsCheckboxList.setItemChecked(1, true);
        if (IMESettingsStore.imeSettings.getCandidates() == Constants.TRUE)
            imeSettingsCheckboxList.setItemChecked(2, true);
    }

    private void showErrorDialog() {
        ErrorDialogFragment errorDialog = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString("errorMessage", errorMessage);
        errorDialog.setArguments(args);
        errorDialog.show(getSupportFragmentManager(), "error");
        errorMessage = Constants.EMPTY_SYM;
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
