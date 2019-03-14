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
package com.aliyun.dataworks.dataservice.sdk.loader.http.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import com.alibaba.fastjson.JSON;

import com.aliyun.dataworks.dataservice.common.http.HttpVisitor;
import com.aliyun.dataworks.dataservice.common.http.constant.Constants;
import com.aliyun.dataworks.dataservice.common.http.constant.ContentType;
import com.aliyun.dataworks.dataservice.common.http.constant.HttpHeader;
import com.aliyun.dataworks.dataservice.common.http.constant.HttpMethod;
import com.aliyun.dataworks.dataservice.common.http.constant.SystemHeader;
import com.aliyun.dataworks.dataservice.common.utils.MessageDigestUtil;
import com.aliyun.dataworks.dataservice.common.utils.SignUtil;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

/**
 * Http工具类
 * @author zishu.lf
 */
@Slf4j
public class DataServiceHttpClient {
    private static final String VERSION="2";

    public static CloseableHttpClient httpClient;
    private static RequestConfig defaultRequestConfig = RequestConfig.custom()
        .setConnectTimeout(Constants.DEFAULT_TIMEOUT)
        .setConnectionRequestTimeout(Constants.DEFAULT_TIMEOUT)
        .setSocketTimeout(Constants.DEFAULT_TIMEOUT).build();

    static {
        if (httpClient == null) {
            synchronized (HttpVisitor.class) {
                if (httpClient == null) {
                    initHttpClient(Constants.DEFAULT_TIMEOUT,Constants.DEFAULT_TIMEOUT,Constants.DEFAULT_TIMEOUT);
                }
            }
        }
    }

    public static void initHttpClient(HttpClientBuilder builder,PoolingHttpClientConnectionManager cm){
        httpClient = builder.setConnectionManager(cm).build();
    }

    public static void initHttpClient(int connectTimeout, int readTimeout, int connectionRequestTimeout){
        HttpClientBuilder builder = HttpClients.custom();
        RequestConfig.Builder requestConfigBuilder = RequestConfig.copy(defaultRequestConfig);
        requestConfigBuilder.setConnectTimeout(connectTimeout);
        requestConfigBuilder.setSocketTimeout(readTimeout);
        requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);

        try {
            SSLContext sslContext = null;
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                new String[] {"TLSv1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            builder.setSSLSocketFactory(sslsf);
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        PoolingHttpClientConnectionManager cm
            = new PoolingHttpClientConnectionManager(20, TimeUnit.SECONDS);
        cm.setMaxTotal(Constants.DEFAULT_MAX_CONNECTION);
        cm.setDefaultMaxPerRoute(Constants.DEFAULT_MAX_CONNECTION);
        builder.disableRedirectHandling();
        builder.setDefaultRequestConfig(requestConfigBuilder.build());
        initHttpClient(builder,cm);
    }
    /**
     * HTTP GET
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static <T> Response<T> httpGet(String host, String path, int connectTimeout, int readTimeout, int connectionRequestTimeout,
                                          Map<String, String> headers,
                                          Map<String, Object> querys,
                                          List<String> signHeaderPrefixList,
                                          String appKey,
                                          String appSecret,
                                          Type type)
            throws Exception {
        headers = initialBasicHeader(HttpMethod.GET, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        HttpGet get = new HttpGet(initUrl(host, path, querys));
        setTimeoutConfig(connectTimeout,readTimeout,connectionRequestTimeout,get);

        for (Map.Entry<String, String> e : headers.entrySet()) {
            get.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        return convert(httpClient.execute(get),type);
    }

    /**
     * HTTP POST表单
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static <T> Response<T> httpPost(String host, String path, int connectTimeout, int readTimeout, int connectionRequestTimeout,
                                           Map<String, String> headers,
                                           Map<String, Object> querys, Map<String, Object> bodys,
                                           List<String> signHeaderPrefixList,
                                           String appKey,
                                           String appSecret,
                                           Type type)
            throws Exception {
        if (headers == null) {
            headers = new HashMap<String, String>(32);
        }

        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_FORM);
        headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, bodys, signHeaderPrefixList, appKey, appSecret);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        setTimeoutConfig(connectTimeout,readTimeout,connectionRequestTimeout,post);

        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        UrlEncodedFormEntity formEntity = buildFormEntity(bodys);
        if (formEntity != null) {
            post.setEntity(formEntity);
        }

        return convert(httpClient.execute(post),type);
    }

    /**
     * Http POST 字符串
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param body
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static <T> Response<T> httpPost(String host, String path, int connectTimeout, int readTimeout,int connectionRequestTimeout ,
                                        Map<String, String> headers,
                                        Map<String, Object> querys,
                                        String body,
                                        List<String> signHeaderPrefixList,
                                        String appKey,
                                        String appSecret,
                                        Type type)
            throws Exception {
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE, ContentType.CONTENT_TYPE_JSON);
    	headers = initialBasicHeader(HttpMethod.POST, path, headers, querys, null, signHeaderPrefixList, appKey, appSecret);

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        setTimeoutConfig(connectTimeout,readTimeout,connectionRequestTimeout,post);

        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), MessageDigestUtil.utf8ToIso88591(e.getValue()));
        }

        if (StringUtils.isNotBlank(body)) {
            post.setEntity(new StringEntity(body, Constants.ENCODING));

        }

        return convert(httpClient.execute(post),type);
    }

    private static void setTimeoutConfig(int connectTimeout, int readTimeout,int connectionRequestTimeout,HttpRequestBase httpRequestBase){
        if(readTimeout>0){
            RequestConfig customConfig = RequestConfig.copy(httpRequestBase.getConfig()==null?defaultRequestConfig:httpRequestBase.getConfig())
                .setSocketTimeout(readTimeout)
                .build();
            httpRequestBase.setConfig(customConfig);
        }
        if(connectTimeout>0){
            RequestConfig customConfig = RequestConfig.copy(httpRequestBase.getConfig()==null?defaultRequestConfig:httpRequestBase.getConfig())
                .setConnectTimeout(connectTimeout)
                .build();
            httpRequestBase.setConfig(customConfig);
        }
        if(connectionRequestTimeout>0){
            RequestConfig customConfig = RequestConfig.copy(httpRequestBase.getConfig()==null?defaultRequestConfig:httpRequestBase.getConfig())
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
            httpRequestBase.setConfig(customConfig);
        }
    }

    /**
     * 构建FormEntity
     * 
     * @param formParam
     * @return
     * @throws UnsupportedEncodingException
     */
    private static UrlEncodedFormEntity buildFormEntity(Map<String, Object> formParam)
            throws UnsupportedEncodingException {
        if (formParam != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : formParam.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, String.valueOf(formParam.get(key))));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, Constants.ENCODING);
            formEntity.setContentType(ContentType.CONTENT_TYPE_FORM);
            return formEntity;
        }

        return null;
    }
    
    private static String initUrl(String host, String path, Map<String, Object> querys) throws UnsupportedEncodingException {
    	StringBuilder sbUrl = new StringBuilder();
    	sbUrl.append(host);
    	if (!StringUtils.isBlank(path)) {
    		sbUrl.append(path);
        }
    	if (null != querys) {
    		StringBuilder sbQuery = new StringBuilder();
        	for (Map.Entry<String, Object> query : querys.entrySet()) {
        		if (0 < sbQuery.length()) {
        			sbQuery.append(Constants.SPE3);
        		}
        		if (StringUtils.isBlank(query.getKey()) && ObjectUtils.notEqual(query.getValue(),null)) {
        			sbQuery.append(query.getValue());
                }
        		if (!StringUtils.isBlank(query.getKey())) {
        			sbQuery.append(query.getKey());
        			if (ObjectUtils.notEqual(query.getValue(),null)) {
        				sbQuery.append(Constants.SPE4);
        				if(query.getValue() instanceof String){
        				    sbQuery.append(URLEncoder.encode(query.getValue().toString(), Constants.ENCODING));
                        }else{
        				    sbQuery.append(query.getValue());
                        }
        			}
                }
        	}
        	if (0 < sbQuery.length()) {
        		sbUrl.append(Constants.SPE5).append(sbQuery);
        	}
        }
    	
    	return sbUrl.toString();
    }
    	

    /**
     * 初始化基础Header
     * @param method
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws MalformedURLException
     */
    private static Map<String, String> initialBasicHeader(String method, String path,
                                                          Map<String, String> headers, 
                                                          Map<String, Object> querys,
                                                          Map<String, Object> bodys,
                                                          List<String> signHeaderPrefixList,
                                                          String appKey, String appSecret)
            throws MalformedURLException {
        if (headers == null) {
            headers = new HashMap<String, String>(32);
        }

        headers.put(SystemHeader.X_CA_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        headers.put(SystemHeader.CLIENT_VERSION, VERSION);
        if(StringUtils.isNotEmpty(appKey)){
            headers.put(SystemHeader.X_CA_KEY, appKey);
        }
        if(StringUtils.isNotEmpty(appSecret)){
            headers.put(SystemHeader.X_CA_SIGNATURE,
                    SignUtil.sign(appSecret, method, path, headers, querys, bodys, signHeaderPrefixList));
        }

        return headers;
    }

    private static <T> Response<T> convert(HttpResponse response,Type type) throws Exception {
    	Response<T> res = new Response();
    	if (null != response && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    		res.setStatusCode(response.getStatusLine().getStatusCode());
    		for (Header header : response.getAllHeaders()) {
    			res.setHeader(header.getName(), MessageDigestUtil.iso88591ToUtf8(header.getValue()));
            }
    		
    		res.setContentType(res.getHeader("Content-Type"));
    		res.setRequestId(res.getHeader("X-Ca-Request-Id"));
    		res.setErrMsg(res.getHeader("X-Ca-Error-Message"));
    		String dataStr = EntityUtils.toString(response.getEntity());
    		res.setData((T)JSON.parseObject(dataStr,type));
    	} else {
    		//服务器无回应
    		throw new Exception(JSON.toJSONString(response.getStatusLine()));
    	}
    	
    	return res;
    }
}