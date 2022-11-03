package com.ruslan.keyboard.clients_impl;

import com.ruslan.keyboard.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientImpl {
    protected OkHttpClient.Builder httpClient;

    protected Retrofit.Builder builder;

    protected Retrofit retrofit;

    protected ClientImpl() {
        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                        .baseUrl(Constants.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder
                        .client(httpClient.build())
                        .build();
    }
}
