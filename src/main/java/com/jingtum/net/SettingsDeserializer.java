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

import com.google.gson.*;
import com.jingtum.model.Settings;
import java.lang.reflect.Type;

/**
 * @author jzhao
 * @version 1.0
 */
public class SettingsDeserializer implements JsonDeserializer<Settings> {
    public Settings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    		throws JsonParseException {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        		.create();
        System.out.println("Deser settings"+json.toString());
        Settings settings = gson.fromJson(json, Settings.class);
        return settings;
    }
}