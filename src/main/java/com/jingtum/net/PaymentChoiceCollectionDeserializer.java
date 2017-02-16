package com.jingtum.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jingtum.model.PaymentChoice;
import com.jingtum.model.PaymentChoiceCollection;

import java.lang.reflect.Type;
import java.util.List;

public class PaymentChoiceCollectionDeserializer implements JsonDeserializer<PaymentChoiceCollection> {
    public PaymentChoiceCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        if (json.isJsonArray()) {
            Type paymentchoiceListType = new TypeToken<List<PaymentChoice>>() {
            }.getType();
            List<PaymentChoice> choice = gson.fromJson(json, paymentchoiceListType);
            PaymentChoiceCollection collection = new PaymentChoiceCollection();
            collection.setData(choice);
            return collection;
        }
        return gson.fromJson(json, typeOfT);
    }
}
