package com.ruslan.keyboard.clients_impl;

import com.ruslan.keyboard.clients.WordClient;
import com.ruslan.keyboard.entities.Word;

import retrofit2.Call;

public class WordClientImpl extends ClientImpl {

    private WordClient mWordClient;

    private Call<Word[]> mCallGet;
    private Call<Word> mCallPost;

    public WordClientImpl() {
//        super("words/");
        super("");
        mWordClient = retrofit.create(WordClient.class);
    }

    public Call<Word[]> getCallGet() {
        return mCallGet;
    }

    public void setCallGet(Integer userId) {
        mCallGet = mWordClient.get();
    }

    public Call<Word> getCallPost() {
        return mCallPost;
    }

    public void setCallPost(Word word) {
        mCallPost = mWordClient.post(word);
    }

//    Call<Word[]> call = wordClient.get(3);
}
