/*
 * Copyright www.jingtum.com Inc. 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jingtum.net;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jingtum.model.Wallet;
import com.jingtum.model.BalanceCollection;
import com.jingtum.model.EffectCollection;
import com.jingtum.model.MemoCollection;
import com.jingtum.model.Notification;
import com.jingtum.model.PaymentCollection;
import com.jingtum.model.RelationCollection;
import com.jingtum.model.TransactionCollection;
import com.jingtum.model.TrustLineCollection;
import com.jingtum.model.OrderCollection;
import com.jingtum.model.OrderBookCollection;
import com.jingtum.model.PaymentChoiceCollection;

import java.lang.reflect.Type;
/**
 * @author jzhao
 * @version 1.0
 * Updated by zpli
 * Added Memo, PaymentChoice
 */
public class WalletDeserializer implements JsonDeserializer<Wallet> {
    public Wallet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        		.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(BalanceCollection.class, new BalanceCollectionDeserializer())
                .registerTypeAdapter(PaymentCollection.class, new PaymentCollectionDeserializer())
                .registerTypeAdapter(OrderCollection.class, new OrderCollectionDeserializer())
                .registerTypeAdapter(OrderBookCollection.class, new OrderBookCollectionDeserializer())
                .registerTypeAdapter(TrustLineCollection.class, new TrustLineCollectionDeserializer())
                .registerTypeAdapter(TransactionCollection.class, new TransactionCollectionDeserializer())
                .registerTypeAdapter(EffectCollection.class, new EffectCollectionDeserializer())
                .registerTypeAdapter(MemoCollection.class, new MemoCollectionDeserializer())
                .registerTypeAdapter(RelationCollection.class, new RelationCollectionDeserializer())
                .registerTypeAdapter(Notification.class, new NotificationDeserializer())
                .registerTypeAdapter(PaymentChoiceCollection.class, new PaymentChoiceCollectionDeserializer())
                .create();
        Wallet wallet = gson.fromJson(json, Wallet.class);
        return wallet;
    }
}
