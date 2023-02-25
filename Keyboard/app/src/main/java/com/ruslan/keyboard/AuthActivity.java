package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.clients_impl.UserClientImpl;
import com.ruslan.keyboard.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private final static String TAG = "AuthActivity";

    private final String mMistakeLoginPassword = "Неверный логин и/или пароль";

    private UserClientImpl mUserClientImpl;
    private DatabaseInteraction mDatabaseInteraction;

    private TextView mMistake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setTitle(R.string.auth_reg_activity);
        mUserClientImpl = new UserClientImpl();
        mDatabaseInteraction = new DatabaseInteraction(this);
        setContentView(R.layout.activity_auth);
        EditText login = findViewById(R.id.login);
        EditText password = findViewById(R.id.password);
        mMistake = findViewById(R.id.mistake);
        Button authButton = findViewById(R.id.authButton);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setLogin(login.getText().toString());
                user.setPassword(password.getText().toString());
                postToApi(user);
            }
        });
        Button regButton = findViewById(R.id.regButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentActivity = new Intent(AuthActivity.this, RegActivity.class);
                startActivity(intentActivity);
            }
        });
    }

    public void postToApi(User user) {
        mUserClientImpl.setCallAuthPost(user);
        mUserClientImpl.getCallAuthPost().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    System.out.println("AuthPost UUUUSSSSEEEERRRR");
                    User u = response.body();
                    if (u.getId() != -1) {
                        mDatabaseInteraction.insertUser(u);
                        System.out.println(u.getLogin());
                        Intent intentActivity = new Intent(AuthActivity.this, IMESettingsActivity.class);
                        intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intentActivity);
                    }
                    else {
                        mMistake.setText(mMistakeLoginPassword);
                    }
                }
                else {
                    mMistake.setText(Constants.ERROR_TRANSFER_DATA);
                    System.out.println("Error AuthPost UUUUSSSSEEEERRRR");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mMistake.setText(Constants.ERROR_CONNECTION);
                System.out.println("Failure AuthPost UUUUSSSSEEEERRRR");
            }
        });
        System.out.println("End AuthPost UUUUSSSSEEEERRRR");
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
