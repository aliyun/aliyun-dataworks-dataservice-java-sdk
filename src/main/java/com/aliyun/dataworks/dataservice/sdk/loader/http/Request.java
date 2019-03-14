/*
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
package com.aliyun.dataworks.dataservice.sdk.loader.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.dataworks.dataservice.model.api.protocol.ApiProtocol;
import com.aliyun.dataworks.dataservice.sdk.loader.http.enums.Method;

/**
 * Request
 * @author zishu.lf
 */
public class Request {

    public Request() {
    }

    public Request(Method method, String host, String path, String appKey, String appSecret, int timeout) {
        this.method = method;
        this.host = host;
        this.path = path;        
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.timeout = timeout;
    }

    //****************************************
    /***
     * 授权baseId
     */
    private String baseId;

    /***
     * 授权的appCode
     */
    private String appCode;

    /***
     * 数据服务的ApiId
     */
    private Long apiId;

    /**
     * 采用的协议
     */
    private ApiProtocol apiProtocol;
    //****************************************
   
    /**
     * （必选）请求方法
     */
    private Method method;

    /**
     * （必选）Host
     */
    private String host;
    
    /**
     * （必选）Path
     */
    private String path;

    /**
     * （必选)APP KEY
     */
    private String appKey;

    /**
     * （必选）APP密钥
     */
    private String appSecret;

    /**
     * （必选）超时时间,单位毫秒,设置零默认使用20秒
     */
    private int timeout;
    /**
     * （必选）超时时间,单位毫秒,设置零默认使用20秒
     */
    private int socketTimeout;
    private int connectionRequestTimeout;
    /**
     * （可选） HTTP头
     */
    private Map<String, String> headers = new HashMap<>();
    
    /**
     * （可选） Querys
     */
    private Map<String, Object> querys = new HashMap<>();

    /**
     * （可选）表单参数
     */
    private Map<String, Object> bodys = new HashMap<>();

    /**
     * （可选）自定义参与签名Header前缀
     */
    private List<String> signHeaderPrefixList;
    
    

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getQuerys() {
        return querys;
    }

    public void setQuerys(Map<String, Object> querys) {
        this.querys = querys;
    }

    public Map<String, Object> getBodys() {
        return bodys;
    }

    public void setBodys(Map<String, Object> bodys) {
        this.bodys = bodys;
    }

    public List<String> getSignHeaderPrefixList() {
        return signHeaderPrefixList;
    }

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public ApiProtocol getApiProtocol() {
        return apiProtocol;
    }

    public void setApiProtocol(ApiProtocol apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

}
