package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.Word;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordClient {

    String res = "words";

    @GET(res)
    Call<Word[]> get(@Query("userId") Integer userId);

    @POST(res)
    Call<Word> post(@Body Word word);

    @PUT(res + "/{id}")
    Call<Word> put(@Path("id") Integer id, @Body Word word);

    @DELETE(res)
    Call<Word[]> delete(@Query("userId") Integer userId);
}
