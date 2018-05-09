package com.nuonuo.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * product子类
 * 多个两个属性 征收代码和征收名称
 * <p>
 * created by zhangbl 2017-11-06
 */
public class ProductOP extends Product implements Serializable {

    @JSONField(name = "u_levy_code")
    protected String levyCode;
    @JSONField(name = "u_levy_name")
    protected String levyName;
    @JSONField(name = "u_levy_flag")
    protected String levyFlag;

    public ProductOP() {
    }

    public ProductOP(Product p) {
        this.num = p.num;
        this.name = p.name;
        this.taxCode = p.taxCode;
        this.rate = p.rate;
        this.smallRate = p.smallRate;
        this.type = p.type;
        this.category = p.category;
        this.choiceCount = p.choiceCount;
        this.choicePercent = p.choicePercent;
        this.recommendFlag = p.recommendFlag;
        this.reScore = p.reScore;
        this.score = p.score;
        this.recommendScore = p.recommendScore;
        this.specialManagement = p.specialManagement;
        this.policy = p.policy;
        this.policyNum = p.policyNum;
    }

    public String getLevyCode() {
        return levyCode;
    }

    public void setLevyCode(String levyCode) {
        this.levyCode = levyCode;
    }

    public String getLevyName() {
        return levyName;
    }

    public void setLevyName(String levyName) {
        this.levyName = levyName;
    }

    public String getLevyFlag() {
        return levyFlag;
    }

    public void setLevyFlag(String levyFlag) {
        this.levyFlag = levyFlag;
    }

    @Override
    public void checkNull() {
        super.checkNull();
        if (null == levyCode) {
            levyCode = "";
        }
        if (null == levyName) {
            levyName = "";
        }
        if (null == levyFlag) {
            levyFlag = "N";
        }
    }

    /**
     * 新增征收品目相关的信息
     *
     * @param levyInfo
     */
    public void addLevyInfo(LevyInfo levyInfo) {
        this.setLevyCode(levyInfo.getLevyCode());
        this.setLevyName(levyInfo.getLevyName());
        this.setLevyFlag(levyInfo.getFlag());
    }
}
