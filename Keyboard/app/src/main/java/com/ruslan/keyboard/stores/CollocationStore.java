package com.ruslan.keyboard.stores;

import com.ruslan.keyboard.entities.Collocation;

import java.util.List;

public class CollocationStore {

    public static List<Collocation> collocations;

    public static void postToStore(Collocation collocation) {
        collocations.add(collocation);
    }

    public static void putToStore(Integer id, Collocation collocation) {
        for (int i = 0; i < collocations.size(); i++) {
            if (collocations.get(i).getId() == id) {
                collocations.get(i).setCount(collocation.getCount());
                break;
            }
        }
    }
}
