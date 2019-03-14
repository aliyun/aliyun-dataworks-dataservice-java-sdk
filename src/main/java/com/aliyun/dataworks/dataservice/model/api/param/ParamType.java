package com.aliyun.dataworks.dataservice.model.api.param;

import com.aliyun.dataworks.dataservice.model.common.IntEnum;

/**
 * API参数类型
 *
 * @author jiuling.ypf 2017年10月23日
 * @version 1.0
 */
public enum ParamType implements IntEnum<ParamType> {
    /**
     * 字符串类型参数
     */
    STRING(0),
    /**
     * Int类型参数
     */
    INT(1),
    /**
     * Long类型参数
     */
    LONG(2),
    /**
     * Float类型参数
     */
    FLOAT(3),
    /**
     * Double类型参数
     */
    DOUBLE(4),
    /**
     * 布尔类型参数
     */
    BOOLEAN(5);

    private int value;

    ParamType(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }

    public static ParamType of(int value) {
        for (ParamType paramType : ParamType.values()) {
            if (paramType.value() == value) {
                return paramType;
            }
        }
        return null;
    }
}
