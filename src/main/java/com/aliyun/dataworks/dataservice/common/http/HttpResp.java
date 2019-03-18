package com.aliyun.dataworks.dataservice.common.http;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.StatusLine;

/**
 *
 * @author lizheng
 * date 2017/11/1
 */
@Data
@NoArgsConstructor
public class HttpResp {

    private StatusLine statusLine;

    private String data;

}
