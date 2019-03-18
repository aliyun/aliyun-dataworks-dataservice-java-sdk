package com.aliyun.dataworks.dataservice.model.api.protocol;

import com.aliyun.dataworks.dataservice.model.api.param.ParamType;
import com.aliyun.dataworks.dataservice.model.common.IntEnum;

/**
 * API类型<br>
 *
 * @author jiuling.ypf
 * date 2017/10/18
 */
public enum ApiProtocol implements IntEnum<ParamType> {

	/***
	 * 位运算来保证协议支持多协议 
	 * HTTP:00000000/00000001
	 * HTTPS:00000010 
	 * RPC:00000100
	 * HSF:00001000
	 */
	HTTP(0), HTTPS(1), RPC(2);

	private int value;

	ApiProtocol(int value) {
		this.value = value;
	}

	@Override
	public int value() {
		return this.value;
	}

	public static ApiProtocol of(int code) {
		for (ApiProtocol apiProtocol : ApiProtocol.values()) {
			if (apiProtocol.value() == code) {
				return apiProtocol;
			}
		}
		return null;
	}

}
