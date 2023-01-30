package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

public class IMESettingsActivity extends AppCompatActivity {

    private final static String TAG = "IMESettingsActivity";

    private DatabaseInteraction mDatabaseInteraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Intent intent = new Intent(this, IME.class);
        startService(intent);

        setTitle(R.string.ime_settings_activity);
        mDatabaseInteraction = new DatabaseInteraction(this);
        mDatabaseInteraction.selectWords();
        if (WordStore.words == null) {
            mDatabaseInteraction.insertWords();
            mDatabaseInteraction.selectWords();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        WordStore.words = null;
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
        String[] imeSettings = new String[] { "Шрифт", "Тема" };
        ListView imeSettingsList = findViewById(R.id.imeSettingsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, imeSettings);
        imeSettingsList.setAdapter(adapter);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
