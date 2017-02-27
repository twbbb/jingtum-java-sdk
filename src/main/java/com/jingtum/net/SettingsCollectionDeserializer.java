package com.jingtum.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jingtum.model.Settings;
import com.jingtum.model.SettingsCollection;

import java.lang.reflect.Type;
import java.util.List;

public class SettingsCollectionDeserializer implements JsonDeserializer<SettingsCollection> {
    public SettingsCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        if (json.isJsonArray()) {
            Type SettingsListType = new TypeToken<List<Settings>>() {
            }.getType();
            List<Settings> Settings = gson.fromJson(json, SettingsListType);
            SettingsCollection collection = new SettingsCollection();
            collection.setData(Settings);
            return collection;
        }
        return gson.fromJson(json, typeOfT);
    }
}
