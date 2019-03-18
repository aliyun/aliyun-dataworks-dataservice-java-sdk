package com.aliyun.dataworks.dataservice.sdk.loader;

import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.aliyun.dataworks.dataservice.common.constants.CommonConstants;
import com.aliyun.dataworks.dataservice.common.http.constant.HttpMethod;
import com.aliyun.dataworks.dataservice.sdk.common.Service;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Request;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Response;
import com.aliyun.dataworks.dataservice.sdk.loader.http.util.DataServiceHttpClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * HTTP协议数据服务执行层
 *
 * @author yangya
 * date 2018/09/10
 */
@Service
public class HttpDataLoader implements DataLoader {
	private static Logger LOGGER = LoggerFactory.getLogger(HttpDataLoader.class);

    /**
     * 获取数据的方法
     * @param request  请求实体
     * @param type 需要返回的对象类型
     * @param <T>  需要返回的对象类型
     * @throws Exception
     */
	@Override
	public <T> Response<T> dataLoad(Request request, Type type) throws Exception {
		if(StringUtils.isNotEmpty(request.getBaseId())){
		    request.getHeaders().put(CommonConstants.BASEID, request.getBaseId());
        }
		if(StringUtils.isNotEmpty(request.getAppCode())){
		    request.getHeaders().put(CommonConstants.APPCODE, request.getAppCode());
		    request.getHeaders().put(CommonConstants.APPCODE_SIGN, "APPCODE "+request.getAppCode());
        }

		switch (request.getMethod()) {
		case HttpMethod.GET:
			Map<String, Object> bodys = request.getBodys();
			bodys.putAll(request.getQuerys());
			request.setQuerys(bodys);
			return get(request, type);
		case HttpMethod.POST:
			return postString(request, type);
		default:
			throw new IllegalArgumentException(String.format("unsupported method:%s", request.getMethod()));
		}
	}

    public <T> Response<T> get(Request request, Type type) throws Exception {
		return DataServiceHttpClient.httpGet(request.getHost(), request.getPath(),
            request.getTimeout(), request.getSocketTimeout(), request.getConnectionRequestTimeout(),
            request.getHeaders(),
            request.getQuerys(), request.getSignHeaderPrefixList(), request.getAppKey(), request.getAppSecret(),
				type);
	}

	public <T> Response<T> postString(Request request, Type type) throws Exception {
		return DataServiceHttpClient.httpPost(request.getHost(), request.getPath(),
            request.getTimeout(), request.getSocketTimeout(), request.getConnectionRequestTimeout(),
            request.getHeaders(),
            request.getQuerys(), JSON.toJSONString(request.getBodys()), request.getSignHeaderPrefixList(),
            request.getAppKey(), request.getAppSecret(), type);
	}

}
