package com.ruslan.keyboard.http;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.Methods;
import com.ruslan.keyboard.entities.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WordsHTTP {
    private String mURL = Constants.IP + "words?userId=";

    public WordsHTTP(int userId) {
        mURL += userId;
    }

    public List<Word> get() {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String jsonString = Methods.getContent(mURL);
//                    mWords = new JSONHelper<Word>().importFromJSON(jsonString, new Word());
////                    List<Word> words = new ArrayList<>();
////                    words.add(new Word(60, 2, "Привет", 1));
////                    System.out.println("))))))))))))))))))))))))))))) " + new JSONHelper<Word>().exportToJSON(words));
////                    System.out.println(mWords.get(0).getWord());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        String jsonString = "";
        try {
            jsonString = Methods.getContent(mURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonString.length() > 0)
            return new JSONHelper<Word>().importFromJSON(jsonString, new Word());
        return null;
    }
}
