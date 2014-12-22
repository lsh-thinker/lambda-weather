package edu.sjtu.se.dclab.entity;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.sjtu.se.dclab.util.DateUtil;

public class Order {
	
	public static final String ORDER_ID = "orderId";
	public static final String CREATE_DATE = "createDate";
	public static final String TOTAL = "total";
	public static final String ORDER_ITEMS = "orderItems";
	public static final String QUANTITY = "quantity";
	
	
	private String orderId;
	private DateTime createDate;
	private float total;
	
	private Map<Goods, Long> orderItems;
	
	
	public Order(JSONObject object){
		orderItems = new HashMap<Goods,Long>();
		
		orderId = String.valueOf(object.get(ORDER_ID));
		createDate = DateUtil.parseTime(String.valueOf(object.get(CREATE_DATE)));
		total = Float.valueOf(String.valueOf(object.get(TOTAL)));
		JSONArray array = (JSONArray)object.get(ORDER_ITEMS);
		for(Object itemObject : array){
			JSONObject item = (JSONObject)itemObject;
			Goods goods = new Goods(item);
			orderItems.put(goods, (Long)item.get(QUANTITY));
		}
		
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public DateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public Map<Goods, Long> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Map<Goods, Long> orderItems) {
		this.orderItems = orderItems;
	}
	
	@Override
	public String toString(){
		JSONObject object = new JSONObject();
		object.put(ORDER_ID, getOrderId());
		object.put(CREATE_DATE, getCreateDate());
		object.put(TOTAL, getTotal());
		JSONArray array = new JSONArray();
		for(Map.Entry<Goods, Long> entry : orderItems.entrySet()){
			JSONObject item = new JSONObject();
			Goods goods = entry.getKey();
			item.put(Goods.GOODS_ID, goods.getGoodsId());
			item.put(Goods.GOODS_NAME, goods.getGoodsName());
			item.put(Goods.GOODS_CATEGORY, goods.getGoodsCategory());
			item.put(QUANTITY, entry.getValue());
			array.add(item);
		}
		object.put(ORDER_ITEMS, array);
		return object.toJSONString();
	}
	
}
