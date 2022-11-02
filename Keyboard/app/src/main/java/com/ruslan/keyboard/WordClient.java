package com.ruslan.keyboard;

import com.ruslan.keyboard.entities.Word;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordClient {
//    @GET("words?userId={id}")
//    Call<Word[]> get(
//      @Query("id") Integer id
//    );
    @GET("?userId=3")
    Call<Word[]> get();
}
