package com.jingtum.net;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jingtum.model.Memo;
import com.jingtum.model.MemoCollection;

public class MemoCollectionDeserializer implements JsonDeserializer<MemoCollection> {
    public MemoCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        if (json.isJsonArray()) {
            Type memoListType = new TypeToken<List<Memo>>() {
            }.getType();
            List<Memo> memo = gson.fromJson(json, memoListType);
            MemoCollection collection = new MemoCollection();
            collection.setData(memo);
            return collection;
        }
        return gson.fromJson(json, typeOfT);
    }
}
