package com.ruslan.keyboard.clients_impl;

import com.ruslan.keyboard.clients.WordClient;
import com.ruslan.keyboard.entities.Word;

import retrofit2.Call;

public class WordClientImpl extends ClientImpl {

    private WordClient mWordClient;

    private Call<Word[]> mCallGet;
    private Call<Word> mCallPost;
    private Call<Word> mCallPut;

    public WordClientImpl() {
        super();
        mWordClient = retrofit.create(WordClient.class);
    }

    public Call<Word[]> getCallGet() {
        return mCallGet;
    }

    public void setCallGet(Integer userId) {
        mCallGet = mWordClient.get(userId);
    }

    public Call<Word> getCallPost() {
        return mCallPost;
    }

    public void setCallPost(Word word) {
        mCallPost = mWordClient.post(word);
    }

    public Call<Word> getCallPut() {
        return mCallPut;
    }

    public void setCallPut(Integer id, Word word) {
        mCallPut = mWordClient.put(id, word);
    }
}
