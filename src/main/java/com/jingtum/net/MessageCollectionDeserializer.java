package com.jingtum.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jingtum.model.Message;
import com.jingtum.model.MessageCollection;

import java.lang.reflect.Type;
import java.util.List;

public class MessageCollectionDeserializer implements JsonDeserializer<MessageCollection> {
    public MessageCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        if (json.isJsonArray()) {
            Type MessageListType = new TypeToken<List<Message>>() {
            }.getType();
            List<Message> Message = gson.fromJson(json, MessageListType);
            MessageCollection collection = new MessageCollection();
            collection.setData(Message);
            return collection;
        }
        return gson.fromJson(json, typeOfT);
    }
}
