package edu.sjtu.se.dclab.order;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.sjtu.se.dclab.agent.RichProcessor;
import edu.sjtu.se.dclab.entity.Order;

public class OrderProcessor extends RichProcessor<String, Order>{

	@Override
	public void init() {
		
	}

	@Override
	public Order process(String str) {
		JSONObject obj=(JSONObject) JSONValue.parse(str);
		Order order = new Order(obj);
		return order;
	}
}
