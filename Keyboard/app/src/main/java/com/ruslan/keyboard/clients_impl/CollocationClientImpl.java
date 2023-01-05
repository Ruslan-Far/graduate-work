package com.ruslan.keyboard.clients_impl;

import com.ruslan.keyboard.clients.CollocationClient;
import com.ruslan.keyboard.entities.Collocation;

import retrofit2.Call;

public class CollocationClientImpl extends ClientImpl {

    private CollocationClient mCollocationClient;

    private Call<Collocation[]> mCallGet;
    private Call<Collocation> mCallPost;
    private Call<Collocation> mCallPut;

    public CollocationClientImpl() {
        super();
        mCollocationClient = retrofit.create(CollocationClient.class);
    }

    public Call<Collocation[]> getCallGet() {
        return mCallGet;
    }

    public void setCallGet(Integer userId, Object expand) {
        mCallGet = mCollocationClient.get(userId, expand);
    }

    public Call<Collocation> getCallPost() {
        return mCallPost;
    }

    public void setCallPost(Collocation collocation) {
        mCallPost = mCollocationClient.post(collocation);
    }

    public Call<Collocation> getCallPut() {
        return mCallPut;
    }

    public void setCallPut(Integer id, Collocation collocation) {
        mCallPut = mCollocationClient.put(id, collocation);
    }
}
