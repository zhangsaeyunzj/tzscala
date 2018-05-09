package com.nuonuo.entity;

/**
 * Created by zhangbl on 2017-03-11.
 * 行政区域ID,行政区域名称,行政区域类型,行政区域类型编号（0-省, 1-市, 2-区）,下级行政区域ID('|'分割)
 */
public class Region {
    private String id;
    private String name;
    private String type;
    private String level;
    private String[] subAreas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String[] getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(String[] subAreas) {
        this.subAreas = subAreas;
    }
}
