package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    private final static String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Log.d(TAG, "onCreate");
        Button authButton = findViewById(R.id.authButton);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentActivity = new Intent(AuthActivity.this, IMESettingsActivity.class);
                intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentActivity);
            }
        });
        Button regButton = findViewById(R.id.regButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentActivity = new Intent(AuthActivity.this, RegActivity.class);
                startActivity(intentActivity);
//                TextView mistake = findViewById(R.id.mistake);
//                mistake.setText("Ошибка");
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
