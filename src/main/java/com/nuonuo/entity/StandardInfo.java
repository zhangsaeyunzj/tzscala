package com.nuonuo.entity;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * 国家编码标准信息类
 * Created by zhangbingling on 2016-05-07.
 */
public class StandardInfo {

    @JSONField(name="i_name")
    private String name;//商品名称
    @JSONField(name="i_explain")
    private String explain;//商品说明
    @JSONField(name="i_keyword")
    private String keyword;//关键字
    @JSONField(name="i_code")
    private String code;//商品编码
    @JSONField(name="i_rate")
    private String rate;//一般纳税人商品税率
    @JSONField(name="i_s_rate")
    private String smallRate;//小规模纳税人税率
    @JSONField(name="i_special_management")
    private String specialManagement;//特殊管理
    @JSONField(name="i_policy")
    private String policy;//优惠政策
    @JSONField(name="i_policy_num")
    private String policyNum;//优惠政策编码
    @JSONField(name="i_flag")
    private String flag;//是否汇总项
    @JSONField(name="i_use")
    private String is_used;//是否可用


    public StandardInfo(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSpecialManagement() {
        return specialManagement;
    }

    public void setSpecialManagement(String specialManagement) {
        this.specialManagement = specialManagement;
    }

    public String getPolicyNum() {
        return policyNum;
    }

    public void setPolicyNum(String policyNum) {
        this.policyNum = policyNum;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSmallRate() {
        return smallRate;
    }

    public void setSmallRate(String smallRate) {
        this.smallRate = smallRate;
    }

    public String getIs_used() {
        return is_used;
    }

    public void setIs_used(String is_used) {
        this.is_used = is_used;
    }
}
