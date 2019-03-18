/**
 * Copyright 2016 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */

package com.aliyun.dataworks.dataservice.common.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import com.aliyun.dataworks.dataservice.common.constants.CommonConstants;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

/**
 * Http爬取类，单例模式。 <br>
 * {@link HttpVisitor}是一个通用的HTTP访问类，该类不需要直接使用。
 * 
 * @author zishu.lf
 * @version 1.0
 */
@Slf4j
public class HttpVisitor implements AutoCloseable {
    private static volatile HttpVisitor visitor = null;

    /** 获得单例的实例 */
    public static HttpVisitor getInstance() {
        if (visitor == null) {
            synchronized (HttpVisitor.class) {
                if (visitor == null) {
                    visitor = new HttpVisitor();
                }
            }
        }
        return visitor;
    }

    private CloseableHttpClient client = null;

    private long counter = 0;

    private HttpVisitor() {
        synchronized (HttpVisitor.class) {
            client = getMultiThreadHttpClient();
        }
    }

    /** 关闭http client */
    @Override
    public void close() {
        synchronized (this) {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
                client = null;
            }
        }
    }

    /**
     * @Deprecated 尽量使用已经封装好的HTTP访问方法，不宜直接业务中调用httpclient。
     */
    public CloseableHttpClient getClient() {
        return getMultiThreadHttpClient();
    }

    /** 执行HTTP请求，得到字节数组 */
    public byte[] doHttpMethod4Bytes(HttpUriRequest request) {
        byte[] bs = null;
        log.info("访问{}：{}", ++counter, request.getURI().toString());
        try (CloseableHttpResponse response = getMultiThreadHttpClient().execute(request)) {
            logResponse(response);
            bs = EntityUtils.toByteArray(response.getEntity());
        } catch (ClientProtocolException e) {
            log.error("Please check your provided http address!", e);
        } catch (IOException e) {
            log.error("Please check your network connection!", e);
        }
        return bs;
    }

    /** 执行HTTP请求，得到字符串 */
    public String doHttpMethod4String(HttpUriRequest request) {
        String str = null;
        log.info("访问{}：{}  header: {}", ++counter, request.getURI().toString(),request.getAllHeaders());
        try (CloseableHttpResponse response = getMultiThreadHttpClient().execute(request)) {
            logResponse(response);
            str = EntityUtils.toString(response.getEntity(), CommonConstants.DEFAULT_CHARSET);
            log.info("响应正文：>>\n" + StringUtils.left(str, 256));
        } catch (ClientProtocolException e) {
            log.error("Please check your provided http address!", e);
        } catch (IOException e) {
            log.error("Please check your network connection!", e);
        }
        return str;
    }

    public HttpResp doHttpMethod(HttpUriRequest request) throws Exception {
        String str = null;
        log.info("访问{}：{}", ++counter, request.getURI().toString());
        try (CloseableHttpResponse response = getMultiThreadHttpClient().execute(request)) {
            logResponse(response);
            str = EntityUtils.toString(response.getEntity(), CommonConstants.DEFAULT_CHARSET);
            log.info("响应正文：>>\n" + StringUtils.left(str, 256));
            HttpResp resp = new HttpResp();
            resp.setStatusLine(response.getStatusLine());
            resp.setData(str);
            return resp;
        }
    }

    /** 获取一个支持HTTPS、支持多线程的HttpClient */
    private CloseableHttpClient getMultiThreadHttpClient() {
        // 如果关闭了，再去调用，则打开一个新的httpclient
        if (client == null) {
            synchronized (HttpVisitor.class) {
                if (client == null) {
                    HttpClientBuilder builder = HttpClients.custom();
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
                    cm.setMaxTotal(Runtime.getRuntime().availableProcessors());
                    builder.disableRedirectHandling();
                    client = builder.setConnectionManager(cm).build();
                }
            }
        }
        return client;
    }

    private void logResponse(HttpResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("响应：>>" + response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (Header h : headers) {
                if ("Set-Cookie".equals(h.getName())) {
                    log.debug(h.getName() + "\t" + h.getValue());
                }
            }
        }
    }
}
