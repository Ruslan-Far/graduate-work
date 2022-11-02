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

    private WordsHTTP wordsHTTP;

    public Orthocorrector(WordsHTTP wordsHTTP) {
        this.wordsHTTP = wordsHTTP;
    }

    public void getInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Word> words = wordsHTTP.get();
                if (words != null)
                    System.out.println(words.get(0).getWord());
                else
                    System.out.println("Null");
            }
        }).start();
    }

    public void postInfo(Word word) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Word w = wordsHTTP.post(word);
                if (w != null)
                    System.out.println(w.getWord());
                else
                    System.out.println("nULL");
            }
        }).start();
    }
}