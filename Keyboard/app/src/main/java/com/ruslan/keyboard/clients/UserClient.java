package com.ruslan.keyboard.clients;

import com.ruslan.keyboard.entities.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    String res = "users";

    @POST(res)
    Call<User> authPost(@Body User user);

    @POST(res + "/reg")
    Call<User> regPost(@Body User user);
}
