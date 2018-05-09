package com.nuonuo.entity;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 商品实体类
 * Created by zhangbingling on 2016-05-06.
 */
public class Product implements Serializable {
    @JSONField(name = "u_num")
    protected String num;//客户端简码
    @JSONField(name = "u_name")
    protected String name;//商品名称
    @JSONField(name = "u_code")
    protected String taxCode;//商品税收编码
    @JSONField(name = "u_rate")
    protected String rate;//一般纳税人税率
    @JSONField(name = "u_s_rate")
    protected String smallRate;//小规模纳税人税率
    @JSONField(name = "u_type")
    protected String type;//商品规格
    @JSONField(name = "u_category")
    protected String category;//商品税收大类
    @JSONField(name = "u_choice_count")
    protected float choiceCount;//用户选择量
    @JSONField(name = "u_choice_percent")
    protected String choicePercent;//用户选择百分比
    @JSONField(name = "u_flag")
    protected String recommendFlag;//推荐标志
    @JSONField(name = "u_reScore")
    protected double reScore;//自定义得分
    @JSONField(name = "u_score")
    protected float score;//搜索引擎得分
    @JSONField(name = "u_recommend_score")
    protected String recommendScore;//推荐分数
    @JSONField(name = "u_special_management")
    protected String specialManagement;//特殊管理
    @JSONField(name = "u_policy")
    protected String policy;//优惠政策
    @JSONField(name = "u_policy_num")
    protected String policyNum;//优惠政策编码

    public Product() {
    }

    public void reset() {
        this.setName("");
        this.setRecommendScore("");
        this.setTaxCode("");
        this.setSpecialManagement("");
        this.setPolicy("");
        this.setPolicyNum("");
        this.setRate("");
        this.setSmallRate("");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getChoiceCount() {
        return choiceCount;
    }

    public void setChoiceCount(float choiceCount) {
        this.choiceCount = choiceCount;
    }

    public String getRecommendFlag() {
        return recommendFlag;
    }

    public void setRecommendFlag(String recommendFlag) {
        this.recommendFlag = recommendFlag;
    }

    public double getReScore() {
        return reScore;
    }

    public void setReScore(double reScore) {
        this.reScore = reScore;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChoicePercent() {
        return choicePercent;
    }

    public void setChoicePercent(String choicePercent) {
        this.choicePercent = choicePercent;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRecommendScore() {
        return recommendScore;
    }

    public void setRecommendScore(String recommendScore) {
        this.recommendScore = recommendScore;
    }


    public String getSpecialManagement() {
        return specialManagement;
    }

    public void setSpecialManagement(String specialManagement) {
        this.specialManagement = specialManagement;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicyNum() {
        return policyNum;
    }

    public void setPolicyNum(String policyNum) {
        this.policyNum = policyNum;
    }

    public String getSmallRate() {
        return smallRate;
    }

    public void setSmallRate(String smallRate) {
        this.smallRate = smallRate;
    }

    /**
     * 为了保证c++客户端不出现null，服务端将null改为""
     */
    public void checkNull() {
        if (null == num) {
            num = "";
        }
        if (null == name) {
            name = "";
        }
        if (null == taxCode) {
            taxCode = "";
        }
        if (null == rate) {
            rate = "";
        }
        if (null == smallRate) {
            smallRate = "";
        }
        if (null == type) {
            type = "";
        }
        if (null == category) {
            category = "";
        }
        if (null == choicePercent) {
            choicePercent = "";
        }
        if (null == recommendFlag) {
            recommendFlag = "0";
        }
        if (null == recommendScore) {
            recommendScore = "";
        }
        if (null == specialManagement) {
            specialManagement = "";
        }
        if (null == policy) {
            policy = "";
        }
        if (null == policyNum) {
            policyNum = "";
        }
    }
}
