package com.aliyun.dataworks.dataservice.sdk;

import com.aliyun.dataworks.dataservice.common.http.constant.HttpMethod;
import com.aliyun.dataworks.dataservice.model.api.protocol.ApiProtocol;
import com.aliyun.dataworks.dataservice.sdk.facade.DataApiClient;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Request;
import com.aliyun.dataworks.dataservice.sdk.loader.http.util.DataServiceHttpClient;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author lifeng
 */

public class DataApiClientTest {
    @Test
    public void testRequest() throws Exception {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.register(BeanScanTest.class);
        annotationConfigApplicationContext.refresh();
        DataApiClient dataApiClient = annotationConfigApplicationContext.getBean(DataApiClient.class);

        DataServiceHttpClient.initHttpClient(5000,5000,5000);
        // http  有参数
        Request request = new Request();
        request.setApiId(1L);
        request.setBaseId("1");
        request.setAppKey("*");
        request.setAppSecret("*");
        request.setAppCode("*");
        request.setHost("*");
        request.setPath("*");
        request.setApiProtocol(ApiProtocol.HTTP);
        request.setMethod(HttpMethod.POST);
        request.getBodys().put("a", "a");
        String result = dataApiClient.dataLoad(request,String.class);
        System.out.println(result);
    }

}
