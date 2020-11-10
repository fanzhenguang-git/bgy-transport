/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.xixunprogram;

/**
 * 设置熙讯节目个数，和播放时长
 *
 * @author: xwb
 * @create: 2019/08/29 15:55
 */
public class MakeProgramFields {

    /**
     * 具体节目的ID
     */
    private Long[] MaterialIds;
    
    /**
     * 节目播放时长
     */
    private int playTime;

    public Long[] getMaterialIds() {
        return MaterialIds;
    }

    public void setMaterialIds(Long[] materialIds) {
        MaterialIds = materialIds;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}
