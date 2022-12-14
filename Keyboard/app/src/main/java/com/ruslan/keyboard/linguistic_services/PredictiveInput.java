package com.ruslan.keyboard.linguistic_services;

import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.view.inputmethod.InputConnection;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.WordStore;

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

    private StringBuilder mLastWord;

    public PredictiveInput(CollocationClientImpl collocationClient,
                           Keyboard.Key can, Keyboard.Key can2, Keyboard.Key can3) {
        mCollocationClientImpl = collocationClient;
        mCan = can;
        mCan2 = can2;
        mCan3 = can3;
        resetFields();
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

    public void postToApi(Collocation collocation) {
        mCollocationClientImpl.setCallPost(collocation);
        mCollocationClientImpl.getCallPost().enqueue(new Callback<Collocation>() {
            @Override
            public void onResponse(Call<Collocation> call, Response<Collocation> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCC2222222222222222222PPPPPPPPPPOOOOOOOOOOOOOOOOOOOO");
                    Collocation c = response.body();
                    System.out.println(c.getWordResources()[0].getWord());
                    System.out.println(c.getWordResources()[1].getWord());
                    CollocationStore.postToStore(c);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC22222222222222222222222EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Collocation> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCC2222222222222222222222222222222FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC222222222222222222222NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    public void putToApi(Integer id, Collocation collocation) {
        mCollocationClientImpl.setCallPut(id, collocation);
        mCollocationClientImpl.getCallPut().enqueue(new Callback<Collocation>() {
            @Override
            public void onResponse(Call<Collocation> call, Response<Collocation> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCC3333333333333333333333333PPPPPPPPPPUUUUUUUUUUUUUUUUUTTTTTTTTTTTTTTTTTTTTTTTT");
                    Collocation c = response.body();
                    System.out.println(c.getWordResources()[0].getWord());
                    System.out.println(c.getWordResources()[1].getWord());
                    CollocationStore.putToStore(c.getId(), c);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC333333333333333333333EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Collocation> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCC333333333333333333333FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC3333333333333333333NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    private void clearHints() {
        IME.sLingServNum = Constants.DEF_LING_SERV_NUM;
        mCan.label = Constants.EMPTY_SYM;
        mCan2.label = Constants.EMPTY_SYM;
        mCan3.label = Constants.EMPTY_SYM;
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
            clearHints();
            return;
        }
        if (mLastWord.length() != 0) {
            StringBuilder penultimateWord = searchPenultimateWord(textBeforeCursor);
            if (penultimateWord == mLastWord) {
                searchLastWord(textBeforeCursor);
                List<Collocation> filteredCollocations
                        = filterCollocationsByTwoWords(penultimateWord.toString(), mLastWord.toString());
                if (filteredCollocations.size() == 0) {
                    Collocation collocation = prepareForPost(penultimateWord.toString());
                    postToApi(collocation);
                }
                else {
                    Collocation collocation = prepareForPut(filteredCollocations.get(0));
                    putToApi(collocation.getId(), collocation);
                }
            }
        }
        searchLastWord(textBeforeCursor);
        hints = getWordsToContinue();
        System.out.println("CCCCCCCCCCCCCCHINTS");
        System.out.println(hints[0]);
        System.out.println(hints[1]);
        System.out.println(hints[2]);
        IME.sLingServNum = Constants.PRED_LING_SERV_NUM;
        checkHintsOnNullAndEmpty(hints);
        mCan.label = hints[0];
        mCan2.label = hints[1];
        mCan3.label = hints[2];
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Collocation prepareForPost(String penultimateWord) {
        Collocation collocation = new Collocation();
        collocation.setPrevId(getIdByWord(penultimateWord));
        collocation.setNextId(getIdByWord(mLastWord.toString()));
        collocation.setCount(1);
        return collocation;
    }

    private Collocation prepareForPut(Collocation collocation) {
        collocation.setCount(collocation.getCount() + 1);
        return collocation;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Collocation> filterCollocationsByTwoWords(String penultimateWord, String lastWord) {
        return CollocationStore.collocations.stream()
                .filter(x -> x.getWordResources()[0].getWord().equals(penultimateWord)
                        && x.getWordResources()[1].getWord().equals(lastWord))
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getIdByWord(String word) {
        return WordStore.words.stream()
                .filter(x -> x.getWord().equals(word))
                .collect(Collectors.toList())
                .get(0)
                .getId();
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
                Collocation removedCollocation = filteredCollocations.stream().max((x, y) ->
                {
                    if (x.getCount() > y.getCount())
                        return 1;
                    return -1;
                }).get();
                hints[i] = removedCollocation.getWordResources()[1].getWord();
                filteredCollocations.remove(removedCollocation);
            }
            else break;
        }
        return hints;
    }

    private void resetFields() {
        mLastWord = new StringBuilder();
    }

    private StringBuilder searchPenultimateWord(String textBeforeCursor) {
        StringBuilder penultimateWord;
        int i;

        penultimateWord = new StringBuilder();
        i = textBeforeCursor.length() - 1;
        for (; i >= 0 && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            penultimateWord.append(textBeforeCursor.charAt(i));
        penultimateWord.reverse();
        return penultimateWord;
    }

    private void searchLastWord(String textBeforeCursor) {
        int i;

        resetFields();
        for (i = textBeforeCursor.length() - 1; i >= 0
                && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            mLastWord.append(textBeforeCursor.charAt(i));
        mLastWord.reverse();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickCanAny(CharSequence hint) {
        List<Collocation> filteredCollocations = filterCollocationsByTwoWords(mLastWord.toString(), hint.toString());
        Collocation collocation = prepareForPut(filteredCollocations.get(0));
        putToApi(collocation.getId(), collocation);
        mIc.commitText(hint.toString(), 0);
        clearHints();
    }
}
