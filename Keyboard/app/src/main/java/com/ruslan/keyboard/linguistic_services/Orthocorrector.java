package com.ruslan.keyboard.linguistic_services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.http.WordsHTTP;

import java.util.List;

//public class Orthocorrector extends Service {
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public void onCreate() {
//        System.out.println("CCCCCCCCCCCCCCCCCCCCCCreated");
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSStarted");
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDestroyed");
//    }
//}

public class Orthocorrector {

    private int userId;

    public Orthocorrector(int userId) {
        this.userId = userId;
    }

    public void downloadInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WordsHTTP wordsHTTP = new WordsHTTP(userId);
                List<Word> words = wordsHTTP.get();
                if (words != null)
                    System.out.println(words.get(0).getWord());
                else
                    System.out.println("Null");
            }
        }).start();
    }
}