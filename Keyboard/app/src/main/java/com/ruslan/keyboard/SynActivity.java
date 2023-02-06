package com.ruslan.keyboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.clients_impl.UserClientImpl;
import com.ruslan.keyboard.entities.User;

public class SynActivity extends AppCompatActivity {

    private final static String TAG = "SynActivity";

    private UserClientImpl mUserClientImpl;
    private DatabaseInteraction mDatabaseInteraction;

    private TextView mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setTitle(R.string.synchronization_activity);
        mUserClientImpl = new UserClientImpl();
        mDatabaseInteraction = new DatabaseInteraction(this);
        setContentView(R.layout.activity_syn);
        Button synButton = findViewById(R.id.synButton);
        synButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synButton.setText("Clicked");
            }
        });
        mMessage = findViewById(R.id.message);
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
