package com.aliyun.dataworks.dataservice.sdk.loader;


import java.lang.reflect.Type;

import com.aliyun.dataworks.dataservice.sdk.loader.http.Request;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Response;

/**
 * @author lifeng
 */
public interface DataLoader {
    /**
     * 加载远程数据
     * @param request  请求实体
     * @param type 需要返回的对象类型
     * @param <T> 需要返回的对象类型
     * @return Response
     * @throws Exception
     */
    <T> Response<T> dataLoad(Request request, Type type) throws Exception;
}
