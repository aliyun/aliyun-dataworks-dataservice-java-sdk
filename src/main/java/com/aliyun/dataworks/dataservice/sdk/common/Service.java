/*
 * Sep 2, 2015
 */
package com.aliyun.dataworks.dataservice.sdk.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0.0
 * @since 1.0.0
 * @author Sidney Xu
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    String value() default "";

}
