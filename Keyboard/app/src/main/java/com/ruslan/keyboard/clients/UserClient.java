package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserClient {

    String res = "users";

    @POST(res)
    Call<User> authPost(@Body User user);

    @POST(res + "/reg")
    Call<User> regPost(@Body User user);

    @DELETE(res + "/{id}")
    Call<User> delete(@Path("id") Integer id);
}
