package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.Collocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryName;

public interface CollocationClient {

    String res = "collocations";

    @GET(res)
    Call<Collocation[]> get(@Query("userId") Integer userId, @QueryName Object expand);

    @POST(res)
    Call<Collocation> post(@Body Collocation collocation);

    @PUT(res + "/{id}")
    Call<Collocation> put(@Path("id") Integer id, @Body Collocation collocation);

    @DELETE(res)
    Call<Collocation[]> delete(@Query("userId") Integer userId);
}
