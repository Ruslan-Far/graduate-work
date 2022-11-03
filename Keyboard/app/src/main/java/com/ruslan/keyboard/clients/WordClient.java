package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.Word;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordClient {
//    @GET("words?userId={id}")
//    Call<Word[]> get(
//      @Query("id") Integer id
//    );
//    @GET("?userId=3")
//    Call<Word[]> get();
//
//    @POST("")
//    Call<Word> post(@Body Word word);
    @GET("words?userId=3")
    Call<Word[]> get();

    @POST("words")
    Call<Word> post(@Body Word word);
}
