/*
 *
 *  * Copyright www.jingtum.com Inc.
 *  *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package com.jingtum;

import com.jingtum.util.Config;
import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by yifan on 11/3/16.
 */
public class ConfigTest {
    @Test
    public void testLoadYamlConfig() throws FileNotFoundException{
        String config_file_path = "src/main/java/com/jingtum/conf/dev.config.yaml";
        Config config = Config.loadConfig(config_file_path);
        assertEquals("wss://tapi.jingtum.com:5443", config.getWebSocketServer());
        assertEquals("https://tapi.jingtum.com", config.getApiServer());
        assertEquals((Integer) 25, config.getActivateAmount());
        assertEquals("v1", config.getApiVersion());
        assertEquals((Integer) 100000000, config.getDefaultTrustLimit());
        assertEquals(1.01, config.getPaymentPathRate(), 0.0000001);
        assertEquals("prefix", config.getPrefix());
        assertEquals("http://tfingate.jingtum.com", config.getTumServer());
    }
}
