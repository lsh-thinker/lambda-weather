package edu.sjtu.se.dclab.entity;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.agent.CoordinatorTest;

public class Goods implements Serializable{
	
	private static final Logger LOG = LoggerFactory.getLogger(Goods.class);
	
	private static final long serialVersionUID = 1L;
	
	public static final String GOODS_ID = "goodsId";
	public static final String GOODS_NAME = "goodsName";
	public static final String GOODS_CATEGORY = "goodsCategory";
	public static final String PRICE = "price";
	
	private String goodsId;
	private String goodsName;
	private String goodsCategory;
	private float price;
	
	public Goods(JSONObject object){
		goodsId = String.valueOf(object.get(Goods.GOODS_ID));
		goodsName = String.valueOf(object.get(Goods.GOODS_NAME));
//		LOG.info(String.valueOf(object.get(PRICE)) + "-------");
//		/price = Float.valueOf(String.valueOf(object.get(PRICE)));
		goodsCategory = String.valueOf(object.get(PRICE));
	}
	
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsCategory() {
		return goodsCategory;
	}
	public void setGoodsCategory(String goodsCategory) {
		this.goodsCategory = goodsCategory;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((goodsId == null) ? 0 : goodsId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Goods other = (Goods) obj;
		if (goodsId == null) {
			if (other.goodsId != null)
				return false;
		} else if (!goodsId.equals(other.goodsId))
			return false;
		return true;
	}
	
	
	
}
