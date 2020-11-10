/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Entity
@Table(name = EntityConstant.TABLE_DEVICE_ALARM)
@EntityListeners(AuditingEntityListener.class)
public class DeviceAlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstant.COLUMN_ID, nullable = false)
    private Integer id;

    // 设备id
    @Column(name = EntityConstant.COLUMN_DEVICE_ID, length = 32)
    private String deviceId;

    // 告警产生时间
    @Column(name = EntityConstant.COLUMN_ALARM_TIME, length = 64)
    private String alarmTime;

    // 信号编号
    @Column(name = EntityConstant.COLUMN_SIGNAL_ID, length = 32)
    private String signalId;

    // 真实值
    @Column(name = EntityConstant.COLUMN_SIGNAL_VAL, length = 32)
    private String signalVal;

    // 报警级别	1 一般报警、2 重要报警、4 报警恢复 、6 紧急报警
    @Column(name = EntityConstant.COLUMN_ALARM_LEVEL)
    private int alarmLevel;

    // 报警内容
    @Column(name = EntityConstant.COLUMN_CONTENT, length = 512)
    private String content;

    // 告警状态	1、报警 0、恢复
    @Column(name = EntityConstant.COLUMN_STATE)
    private int state;
}
