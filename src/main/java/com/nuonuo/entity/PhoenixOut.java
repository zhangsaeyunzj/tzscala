package com.nuonuo.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class PhoenixOut {

	@JSONField(name = "PK")
	private String PK;
	@JSONField(name = "tax_num")
	private String tax_num;  //税号
	@JSONField(name = "code")
	private String code;  //编码
	@JSONField(name = "name")
	private String name; //名称
	@JSONField(name = "sim_code")
	private String sim_code; //简码
	@JSONField(name = "category")
	private String category;  //商品税目
	@JSONField(name = "rate")
	private String rate;   //税率
	@JSONField(name = "style")
	private String style;  //规格型号
	@JSONField(name = "units")
	private String units; //计量单位
	@JSONField(name = "price")
	private String price; //单价
	@JSONField(name = "tax_mark")
	private String tax_mark; //含税价标志
	@JSONField(name = "tax_category")
	private String tax_category; //税收名称大类
	@JSONField(name = "tax_code")
	private String tax_code; //税收分类编码
	@JSONField(name = "score")
	private String score;  //推荐评分
	@JSONField(name = "policy")
	private String policy; //享受优惠政策
	
	
	public String getPK() {
		return PK;
	}
	public void setPK(String pK) {
		PK = pK;
	}
	public String getTax_num() {
		return tax_num;
	}
	public void setTax_num(String tax_num) {
		this.tax_num = tax_num;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSim_code() {
		return sim_code;
	}
	public void setSim_code(String sim_code) {
		this.sim_code = sim_code;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTax_mark() {
		return tax_mark;
	}
	public void setTax_mark(String tax_mark) {
		this.tax_mark = tax_mark;
	}
	public String getTax_category() {
		return tax_category;
	}
	public void setTax_category(String tax_category) {
		this.tax_category = tax_category;
	}
	public String getTax_code() {
		return tax_code;
	}
	public void setTax_code(String tax_code) {
		this.tax_code = tax_code;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}





	
	
}
