/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.vo.geo;

import lombok.Data;

import java.io.Serializable;

/**
 * 智能地磁_SS-IOT-006
 *
 * @author hexing
 * @date 2019/12/26 10:59
 */
@Data
public class GetBusinessVo implements Serializable {
    /**
     * 电池电压
     */
    private Double battery_voltage;

    /**
     * 电池电量
     */
    private Integer battery_value;

    /**
     * 设备触发时间
     **/
    private Long ptime;

    /**
     * 车位状态 枚举值 : 0--无车 1--有车
     */
    private Integer parking_state;

    /**
     * 故障0--正常 1--传感器故障 2--电池故障
     */
    private Integer error_code;

    /**
     * 车位变化 枚举值 : 0--出车 1--入车
     */
    private Integer parking_change;

    /**
     * 当前磁场值
     */
    private Integer magnetic_value;
}
