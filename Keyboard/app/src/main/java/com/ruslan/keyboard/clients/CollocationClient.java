package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.Collocation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryName;

public interface CollocationClient {

    String res = "collocations";

    @GET(res)
    Call<Collocation[]> get(@Query("userId") Integer userId, @QueryName Object expand);

}
