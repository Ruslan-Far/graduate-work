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
import androidx.appcompat.app.AppCompatDelegate;

import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;

public class IMESettingsActivity extends AppCompatActivity {

    private final static String TAG = "IMESettingsActivity";

    private DatabaseInteraction mDatabaseInteraction;

    public static String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
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
        errorMessage = Constants.EMPTY_SYM;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        UserStore.user = null;
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
        String[] ime_settings_checkbox = getResources().getStringArray(R.array.ime_settings_checkbox);
        ListView imeSettingsCheckboxList = findViewById(R.id.imeSettingsCheckboxList);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                    ime_settings_checkbox);
        imeSettingsCheckboxList.setAdapter(adapter);
        imeSettingsCheckboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem;

                selectedItem = adapter.getItem(position);
                if (imeSettingsCheckboxList.isItemChecked(position)) {
                    if (selectedItem.equals(getString(R.string.sound_checkbox))) {
                        System.out.println("Установить звук");
                    }
                }
                else {
                    if (selectedItem.equals(getString(R.string.sound_checkbox))) {
                        System.out.println("Отключить звук");
                    }
                }
            }
        });
        imeSettingsCheckboxList.setItemChecked(0, true);
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

    public void showErrorDialog() {
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
