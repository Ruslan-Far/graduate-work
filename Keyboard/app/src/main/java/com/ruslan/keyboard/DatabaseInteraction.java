package com.ruslan.keyboard;

import android.content.Context;

import com.ruslan.keyboard.entities.User;
import com.ruslan.keyboard.repos.UserRepo;
import com.ruslan.keyboard.stores.UserStore;

public class DatabaseInteraction {

    private UserRepo mUserRepo;

    public DatabaseInteraction(Context context) {
        mUserRepo = new UserRepo(context);
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
}
