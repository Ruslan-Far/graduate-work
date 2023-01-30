package com.ruslan.keyboard;

import android.content.Context;

import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.entities.User;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.repos.CollocationRepo;
import com.ruslan.keyboard.repos.WordRepo;
import com.ruslan.keyboard.repos.UserRepo;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

public class DatabaseInteraction {

    private UserRepo mUserRepo;
    private WordRepo mWordRepo;
    private CollocationRepo mCollocationRepo;

    public DatabaseInteraction(Context context) {
        mUserRepo = new UserRepo(context);
        mWordRepo = new WordRepo(context);
        mCollocationRepo = new CollocationRepo(context);
    }

    public void selectUser() {
        mUserRepo.open();
        UserStore.user = mUserRepo.select();
        mUserRepo.close();
    }

    public void insertUser(User user) {
        mUserRepo.open();
        mUserRepo.insert(user);
        mUserRepo.close();
    }

    public void deleteUser() {
        mUserRepo.open();
        mUserRepo.delete();
        UserStore.user = null;
        mUserRepo.close();
    }

    public void selectWords() {
        mWordRepo.open();
        WordStore.words = mWordRepo.select();
        mWordRepo.close();
    }

    public void insertWord(Word word) {
        WordStore.postToStore(word);
        mWordRepo.open();
        mWordRepo.insert(word);
        mWordRepo.close();
    }

    public void insertWords() {
        mWordRepo.open();
        mWordRepo.insertAll();
        mWordRepo.close();
    }

    public void updateWord(Integer id, Word word) {
        WordStore.putToStore(id, word);
        mWordRepo.open();
        mWordRepo.update(id, word);
        mWordRepo.close();
    }

    public void selectCollocations() {
        mCollocationRepo.open();
        CollocationStore.collocations = mCollocationRepo.select();
        mCollocationRepo.close();
    }

    public void insertCollocation(Collocation collocation) {
        CollocationStore.postToStore(collocation);
        mCollocationRepo.open();
        mCollocationRepo.insert(collocation);
        mCollocationRepo.close();
    }

    public void updateCollocation(Integer id, Collocation collocation) {
        CollocationStore.putToStore(id, collocation);
        mCollocationRepo.open();
        mCollocationRepo.update(id, collocation);
        mCollocationRepo.close();
    }
}
