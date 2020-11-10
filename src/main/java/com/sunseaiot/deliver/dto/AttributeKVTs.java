/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dto;

import lombok.Data;

/**
 * @author hupan
 * @date 2019-08-15.
 */
@Data
public class AttributeKVTs {

    // 属性key
    private String key;

    // 显示名称
    private String keyName;

    // 时间戳
    private Long timestamp;

    // 属性值
    private TypeValue typeValue;
}
