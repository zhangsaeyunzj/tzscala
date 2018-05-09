package com.nuonuo.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 征收品目实体类
 * <p>
 * created by zhangbl 2017-11-06
 */
public class LevyInfo {
    @JSONField(name = "l_code")
    private String levyCode;//征收品目代码
    @JSONField(name = "l_name")
    private String levyName;//征收品目名称
    @JSONField(name = "l_flag")
    private String flag;//营改增标志位
    @JSONField(name = "l_tax_code")
    private String taxCode;//税收分类编码


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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }
}
