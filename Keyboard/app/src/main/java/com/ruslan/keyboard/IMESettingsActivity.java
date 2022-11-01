package com.ruslan.keyboard;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class IMESettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ime_settings);
        setTitle(R.string.ime_settings_activity);
        Intent intent = new Intent(this, IME.class);
        startService(intent);
    }

//    public void onClick(View view) {
//        TextView textView = findViewById(R.id.textView);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
////                    String s = getContent("http://192.168.1.101:8080/words?userId=3");
////                    String s = getContent("http://192.168.161.71:8080/words?userId=3");
////                    String s = getContent("http://192.168.1.101:8081/");
////                    String s = getContent("http://project6184112.tilda.ws/");
////                    String s = getContent("https://stackoverflow.com/");
//                    textView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            textView.setText(s);
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}
