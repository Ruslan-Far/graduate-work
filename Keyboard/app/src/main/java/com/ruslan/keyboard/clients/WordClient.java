package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.Word;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WordClient {

    String res = "words";

    @GET(res)
    Call<Word[]> get(@Query("userId") Integer userId);

    @POST(res)
    Call<Word> post(@Body Word word);
}
