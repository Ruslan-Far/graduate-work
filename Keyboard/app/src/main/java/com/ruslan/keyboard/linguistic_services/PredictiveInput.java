package com.ruslan.keyboard.linguistic_services;

import android.view.inputmethod.InputConnection;
import android.widget.Button;

import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.stores.CollocationStore;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictiveInput {

    private CollocationClientImpl mCollocationClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private InputConnection mIc;

    public PredictiveInput(CollocationClientImpl collocationClient, Button btn, Button btn2, Button btn3) {
        mCollocationClientImpl = collocationClient;
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
    }

    public InputConnection getIc() {
        return mIc;
    }

    public void setIc(InputConnection ic) {
        mIc = ic;
    }

    public void getFromApi(Integer userId, Object expand) {
        mCollocationClientImpl.setCallGet(userId, expand);
        mCollocationClientImpl.getCallGet().enqueue(new Callback<Collocation[]>() {
            @Override
            public void onResponse(Call<Collocation[]> call, Response<Collocation[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    CollocationStore.collocations = new ArrayList<>(Arrays.asList(response.body()));
                    for (int i = 0; i < CollocationStore.collocations.size(); i++) {
                        System.out.println(CollocationStore.collocations.get(i).getNextId() + " " + CollocationStore.collocations.get(i).getPrevId());
                        System.out.println(CollocationStore.collocations.get(i).getWordResources()[0].getWord());
                        System.out.println(CollocationStore.collocations.get(i).getWordResources()[1].getWord());
                    }
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Collocation[]> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }
}
