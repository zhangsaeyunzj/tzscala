package com.nuonuo.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * product子类
 * 新增一个属性 税收分类名称
 * <p>
 * created by heixn 2017-11-22
 */
public class ProductDo extends Product implements Serializable {

    @JSONField(name = "u_taxname")
    protected String taxName;

    public ProductDo() {
    }

    public ProductDo(Product p) {
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

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    @Override
    public void checkNull() {
        super.checkNull();
        if (null == taxName) {
            taxName = "";
        }
    }

}
