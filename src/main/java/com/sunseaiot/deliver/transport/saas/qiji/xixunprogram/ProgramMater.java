/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.xixunprogram;

/**
 * 具体的节目
 *
 * @author: xwb
 * @create: 2019/08/29 15:56
 */
public class ProgramMater {

    /**
     * 节目ID
     */
    private Long id;

    /**
     * 节目类型 type=1 是image,type=2 是video
     */
    private Integer type;

    /**
     * 节目名称
     */
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
