package com.ruslan.keyboard.clients_impl;

import com.ruslan.keyboard.clients.UserClient;
import com.ruslan.keyboard.entities.User;

import retrofit2.Call;

public class UserClientImpl extends ClientImpl {

    private UserClient mUserClient;

    private Call<User> mCallAuthPost;
    private Call<User> mCallRegPost;

    public UserClientImpl() {
        super();
        mUserClient = retrofit.create(UserClient.class);
    }

    public Call<User> getCallAuthPost() {
        return mCallAuthPost;
    }

    public void setCallAuthPost(User user) {
        mCallAuthPost = mUserClient.authPost(user);
    }

    public Call<User> getCallRegPost() {
        return mCallRegPost;
    }

    public void setCallRegPost(User user) {
        mCallRegPost = mUserClient.regPost(user);
    }
}
