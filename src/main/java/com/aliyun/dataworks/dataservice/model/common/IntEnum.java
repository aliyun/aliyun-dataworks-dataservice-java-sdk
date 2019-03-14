package com.aliyun.dataworks.dataservice.model.common;

/**
 * @author zishu.lf
 */
public interface IntEnum<E extends Enum<E>> {
    /**
     * h获取枚举的值
     * @return
     */
    int value();

}
