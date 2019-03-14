package com.aliyun.dataworks.dataservice.sdk.loader.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zishu.lf
 */
public class Response<T> {
    private int statusCode;
    private String contentType;
    private Map<String, String> headers;
    private T data;
    private String requestId;

    private String errMsg = "success";

    public Response() {
		
    }

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public String getHeader(String key) {
		if (null != headers) {
			return headers.get(key);
		} else {
			return null;
		}
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void setHeader(String key, String value) {
		if (null == this.headers) {
			this.headers = new HashMap<String, String>(32);
		}
		this.headers.put(key, value);
	}

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
