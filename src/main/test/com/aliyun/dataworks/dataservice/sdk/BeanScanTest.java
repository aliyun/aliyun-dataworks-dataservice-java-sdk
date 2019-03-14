package com.aliyun.dataworks.dataservice.sdk;

import com.aliyun.dataworks.dataservice.sdk.common.BeanRegistryProcessor;
import com.aliyun.dataworks.dataservice.sdk.facade.DataApiClient;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lifeng
 */
@Configuration
public class BeanScanTest {
    static DataApiClient dataServiceClient;

	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
		annotationConfigApplicationContext.register(BeanScanTest.class);
		annotationConfigApplicationContext.refresh();
        dataServiceClient = annotationConfigApplicationContext.getBean(DataApiClient.class);

	}

	@Bean
	public static BeanRegistryProcessor beanScannerConfigurer() {
		return new BeanRegistryProcessor();
	}

}