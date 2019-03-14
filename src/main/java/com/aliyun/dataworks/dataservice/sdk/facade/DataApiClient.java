package com.aliyun.dataworks.dataservice.sdk.facade;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.aliyun.dataworks.dataservice.model.api.protocol.ApiProtocol;
import com.aliyun.dataworks.dataservice.sdk.common.Service;
import com.aliyun.dataworks.dataservice.sdk.loader.DataLoader;
import com.aliyun.dataworks.dataservice.sdk.loader.HttpDataLoader;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Request;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static com.aliyun.dataworks.dataservice.model.api.protocol.ApiProtocol.*;

/**
 * @author @author yangya.yy
 * @since 2018/09/10
 */
@Service
public class DataApiClient {

	@Autowired
	private HttpDataLoader httpDataLoader;

	public DataLoader getLoader(Request request) {
		switch (request.getApiProtocol()) {
		case HTTP:
			return httpDataLoader;
		default:
			throw new RuntimeException("not supported protocol");
		}
	}

    public HashMap dataLoad(Request request) throws Exception {
        checkParam(request);
        return dataLoadResponse(request).getData();
    }

    public <T> T dataLoad(Request request, Type type) throws Exception {
        checkParam(request);
        return (T)dataLoadResponse(request,type).getData();
    }

    public Response<HashMap> dataLoadResponse(Request request) throws Exception {
        checkParam(request);
        Response<HashMap> response = getLoader(request).dataLoad(request,HashMap.class);
        return response;
    }

    public <T> Response<T> dataLoadResponse(Request request, Type type) throws Exception {
        checkParam(request);
        Response<T> response = getLoader(request).dataLoad(request,type);
        return response;
    }

	private void checkParam(Request request) {
		if(request.getApiProtocol()==null){
            Assert.notNull(request.getApiProtocol(),"request.apiprotocol can not be empty");
        }
		if (HTTP.equals(request.getApiProtocol())) {
			Assert.notNull(request.getMethod(), "request.method can not be empty");
		}
	}

}
