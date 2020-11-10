/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.config;

import com.sunseaiot.deliver.config.geo.Geo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * aep配置信息
 *
 * @Author: wanglong
 * @Date: 2020/1/10
 **/
@Data
public class ApplicationProperties {

    @Value("${deliver.aep.app-secret}")
    private String appSecret;

    @Value("${deliver.aep.app-key}")
    private String appKey;

    /**
     * 地磁
     */
    private Geo geo;

    /**
     * aep基本参数
     */
    public static class Protocal {
        /**
         * 产品id
         */
        private String productId;
        /**
         * 产品唯一key
         */
        private String masterKey;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getMasterKey() {
            return masterKey;
        }

        public void setMasterKey(String masterKey) {
            this.masterKey = masterKey;
        }
    }
}
