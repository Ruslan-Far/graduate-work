package com.ruslan.keyboard.http;

import com.google.gson.Gson;
import com.ruslan.keyboard.entities.BaseEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JSONHelper<T extends BaseEntity> {
    public String exportToJSON(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
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
        return list;
    }
}
