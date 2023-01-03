package com.ruslan.keyboard.linguistic_services;

import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.stores.CollocationStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictiveInput {

    private CollocationClientImpl mCollocationClientImpl;

    private Keyboard.Key mCan;
    private Keyboard.Key mCan2;
    private Keyboard.Key mCan3;

    private InputConnection mIc;

//    private StringBuilder mLastOther;
    private StringBuilder mLastWord;

    public PredictiveInput(CollocationClientImpl collocationClient,
                           Keyboard.Key can, Keyboard.Key can2, Keyboard.Key can3) {
        mCollocationClientImpl = collocationClient;
        mCan = can;
        mCan2 = can2;
        mCan3 = can3;
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

    private void checkHintsOnNullAndEmpty(String[] hints) {
        for (int i = 0; i < hints.length; i++) {
            if (hints[i] == null || hints[i].length() == 0)
                hints[i] = Constants.EMPTY_SYM;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void process() {
        String textBeforeCursor;
        String[] hints;

        textBeforeCursor = mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString();
        if (textBeforeCursor.length() == 0)
            return;
        if (Character.isLetter(textBeforeCursor.charAt(textBeforeCursor.length() - 1))) {

        }
        searchLastWord(textBeforeCursor);
        hints = getWordsToContinue();
        checkHintsOnNullAndEmpty(hints);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getWordsToContinue() {
        String[] hints;
        List<Collocation> filteredCollocations;

        hints = new String[Constants.NUMBER_OF_HINTS];
        filteredCollocations = CollocationStore.collocations.stream()
                .filter(x -> x.getWordResources()[0].getWord().equals(mLastWord.toString()))
                .collect(Collectors.toList());
        for (int i = 0; i < hints.length; i++) {
            if (filteredCollocations.size() != 0) {
//                hints[i] = maxCount(filteredCollocations);
                Collocation removeCollocation = filteredCollocations.stream().max((x, y) ->
                {
                    if (x.getCount() > y.getCount())
                        return 1;
                    return -1;
                }).get();
                hints[i] = removeCollocation.getWordResources()[1].getWord();
                filteredCollocations.remove(removeCollocation);
            }
            else break;
        }
        return hints;
    }

//    private String maxCount(List<Collocation> filteredCollocations) {
//        Integer max = -1;
//        Collocation removeCollocation = null;
//        String wordContinue;
//
//        for (Collocation collocation : filteredCollocations) {
//            if (collocation.getCount() > max) {
//                max = collocation.getCount();
//                removeCollocation = collocation;
//            }
//        }
//        wordContinue = removeCollocation.getWordResources()[1].getWord();
//        filteredCollocations.remove(removeCollocation);
//        return wordContinue;
//    }

    private void resetFields() {
        mLastWord = new StringBuilder();
    }

    private void searchLastWord(String textBeforeCursor) {
        int i;

        resetFields();
        for (i = textBeforeCursor.length() - 1; i >= 0
                && !Character.isLetter(textBeforeCursor.charAt(i)); i--)
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            mLastWord.append(textBeforeCursor.charAt(i));
        mLastWord.reverse();
    }

    public void clickCanAny(CharSequence hint) {

        mIc.commitText(hint.toString(), 0);
    }
}
