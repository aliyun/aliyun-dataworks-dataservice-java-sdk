package com.aliyun.dataworks.dataservice.sdk.common;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author lifeng
 */
public class CustomizeFactoryBean<T> implements InitializingBean, FactoryBean<T> {

		private String innerClassName;

		public void setInnerClassName(String innerClassName) {
            this.innerClassName = innerClassName;
		}

		@Override
        public T getObject() throws Exception {
			Class innerClass = Class.forName(innerClassName);
			if (innerClass.isInterface()) {
				return (T) CustomizeInterfaceProxy.newInstance(innerClass);
			} else {
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(innerClass);
				enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
				enhancer.setCallback(new CustomizeMethodInterceptor());
				return (T) enhancer.create();
			}
		}

		@Override
        public Class<?> getObjectType() {
			try {
				return Class.forName(innerClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
        public boolean isSingleton() {
			return true;
		}

		@Override
        public void afterPropertiesSet() throws Exception {

		}
	}