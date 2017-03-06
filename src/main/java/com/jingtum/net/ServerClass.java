
/**
 * Created by zpli on 2/7/17.
 */
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
import com.jingtum.model.BalanceCollection;
import com.jingtum.model.EffectCollection;
import com.jingtum.model.OrderCollection;
import com.jingtum.model.OrderBookCollection;
import com.jingtum.model.TransactionCollection;
import com.jingtum.model.JingtumObject;
import com.jingtum.model.MemoCollection;
import com.jingtum.model.PaymentCollection;
import com.jingtum.model.PaymentChoiceCollection;
import com.jingtum.model.RelationCollection;
import com.jingtum.model.SettingsCollection;
import com.jingtum.model.Settings;

/**
 * @author zp li.
 * @version 1.0
 *
 * Extends the abstract class when you need request anything from jingtum
 * Contains the basic info for servers
 */
public class ServerClass extends JingtumObject {
    /**
     * URLEncoder charset
     */
    public static final String CHARSET = "UTF-8";

    protected String serverURL; // server url

    protected ServerClass(String in_url){
        serverURL = in_url;
    }

    public void setServerURL(String in_url){
        serverURL = in_url;
    }

    public String getServerURL(){
        return serverURL;
    }
    
    /**
     * Gson object use to transform json string to Jingtum object
     * Added Memo
     *            discard the
     *            .registerTypeAdapter(PaymentChoiceCollection.class, new PaymentChoiceCollectionDeserializer())
     *                        .registerTypeAdapter(PaymentCollection.class, new PaymentCollectionDeserializer())
     * canceled
     * .registerTypeAdapter(SettingsCollection.class, new SettingsCollectionDeserializer())
     */
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(EffectCollection.class, new EffectCollectionDeserializer())
            .registerTypeAdapter(BalanceCollection.class, new BalanceCollectionDeserializer())
            .registerTypeAdapter(RelationCollection.class, new RelationCollectionDeserializer())
            .registerTypeAdapter(OrderCollection.class, new OrderCollectionDeserializer())
            .registerTypeAdapter(OrderBookCollection.class, new OrderBookCollectionDeserializer())
            .registerTypeAdapter(TransactionCollection.class, new TransactionCollectionDeserializer())
            .registerTypeAdapter(MemoCollection.class, new MemoCollectionDeserializer())
            .registerTypeAdapter(PaymentCollection.class, new PaymentCollectionDeserializer())
            .registerTypeAdapter(Settings.class, new SettingsDeserializer())
            .create();
}
