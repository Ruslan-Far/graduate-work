package com.ruslan.keyboard.http;

import com.google.gson.Gson;
import com.ruslan.keyboard.entities.BaseEntity;
import com.ruslan.keyboard.entities.Word;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JSONHelper<T extends BaseEntity> {
    public String exportToJSON(List<T> dataList) {
        Gson gson = new Gson();
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .create();
        return gson.toJson(new Word(60, 2, "Привет", 1));
    }

    public List<T> importFromJSON(String jsonString, T t) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<>();
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        String[] jsonArray = jsonString.split(Pattern.quote("}"));

        for (int i = 0; i < jsonArray.length - 1; i++) {
            if (i != 0)
                jsonArray[i] = jsonArray[i].substring(1);
            jsonArray[i] += "}";
            t = gson.fromJson(jsonArray[i], (Type) t.getClass());
            list.add(t);
        }
//        t = gson.fromJson("{\"id\":51,\"userId\":3,\"word\":\"Руслан\",\"count\":1}", (Type) t.getClass());
        return list;
    }
}
