package com.ruslan.keyboard;

import com.ruslan.keyboard.entities.Word;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordClientImpl {
    private String API_BASE_URL = Constants.API_BASE_URL + "words/";

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    Retrofit.Builder builder =
            new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit =
            builder
                .client(httpClient.build())
                .build();

    WordClient wordClient = retrofit.create(WordClient.class);

//    Call<Word[]> call = wordClient.get(3);
    Call<Word[]> call = wordClient.get();
}
