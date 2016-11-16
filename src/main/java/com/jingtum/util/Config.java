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

package com.jingtum.util;


import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by yifan on 11/3/16.
 */
public class Config {

    private String webSocketServer;
    private String apiServer;
    private Integer activateAmount;
    private Integer defaultTrustLimit;
    private Float paymentPathRate;
    private String prefix;
    private String apiVersion;
    private String ttServer;

    public String getWebSocketServer() {
        return webSocketServer;
    }

    public void setWebSocketServer(String webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    public String getApiServer() {
        return apiServer;
    }

    public void setApiServer(String apiServer) {
        this.apiServer = apiServer;
    }

    public Integer getActivateAmount() {
        return activateAmount;
    }

    public void setActivateAmount(Integer activateAmount) {
        this.activateAmount = activateAmount;
    }

    public Integer getDefaultTrustLimit() {
        return defaultTrustLimit;
    }

    public void setDefaultTrustLimit(Integer defaultTrustLimit) {
        this.defaultTrustLimit = defaultTrustLimit;
    }

    public Float getPaymentPathRate() {
        return paymentPathRate;
    }

    public void setPaymentPathRate(Float paymentPathRate) {
        this.paymentPathRate = paymentPathRate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getTtServer() {
        return ttServer;
    }

    public void setTtServer(String ttServer) {
        this.ttServer = ttServer;
    }

    public static Config loadConfig(String config_file_path) throws FileNotFoundException {
        return Config._load_yaml(config_file_path);
    }

    private static Config _load_yaml(String config_file_path) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(new FileInputStream(new File(config_file_path)), Config.class);
    }
}
