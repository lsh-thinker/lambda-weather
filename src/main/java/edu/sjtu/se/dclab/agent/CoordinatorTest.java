package edu.sjtu.se.dclab.agent;

import java.net.MalformedURLException;
import java.net.URL;

import edu.sjtu.se.dclab.entity.Order;
import edu.sjtu.se.dclab.order.OrderProcessor;

public class CoordinatorTest {
	
	public static void main(String[] args){
		final String host = "www.weather.com.cn";
		final String protocol= "http";
		final String kafkaTopic = "order";
		
		HttpFetcher fetcher = new HttpFetcher(new URLGenerator() {
			@Override
			public URL getUrl() {
				StringBuilder builder = new StringBuilder("/data/sk/");
				builder.append("101010100");
				builder.append(".html");
				URL url = null;
				try {
					url = new URL(protocol, host, builder.toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		
		Coordinator<String, Order> coordinator = new Coordinator<String,Order>();
		coordinator.addFetcher(fetcher);
		coordinator.addProcessor(new OrderProcessor());
		coordinator.addSender(new KafkaSender<Order>(kafkaTopic));
		coordinator.launch();
		
		
		
	}
}
