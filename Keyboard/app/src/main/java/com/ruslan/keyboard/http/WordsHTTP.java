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
    private String mGetURL = Constants.IP + "words?userId=";
    private String mPostURL = Constants.IP + "words";

    public WordsHTTP(int userId) {
        mGetURL += userId;
    }

    public List<Word> get() {
        String jsonString = "";

        try {
            jsonString = Methods.getContent(mGetURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonString.length() > 0)
            return new JSONHelper<Word>().importFromJSON(jsonString, new Word());
        return null;
    }

    public Word post(Word word) {
        String jsonString = new JSONHelper<Word>().exportToJSON(word);

//        try {
            jsonString = Methods.postContent(mPostURL, jsonString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (jsonString.length() > 0)
            return new JSONHelper<Word>().importFromJSON(jsonString, new Word()).get(0);
        return null;
    }
}
