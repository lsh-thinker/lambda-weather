package edu.sjtu.se.dclab.agent;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.TridentState;
import storm.trident.operation.BaseFilter;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.StateFactory;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;
import storm.trident.tuple.TridentTuple;
import trident.cassandra.CassandraState;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.sjtu.se.dclab.entity.Order;
import edu.sjtu.se.dclab.storm.CountByFieldDefinition;
import edu.sjtu.se.dclab.storm.StormService;

public class CoordinatorTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(CoordinatorTest.class);
	
	private final static DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd/HH:mm");
	
	public static class ToObjectFunction extends BaseFunction{
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String str = String.valueOf(tuple.get(0));
			JSONObject obj = (JSONObject)JSONValue.parse(str);
			collector.emit(new Values(new Order(obj)));
		}
	}
	
	public static class ObjectFilter extends BaseFilter{
		@Override
		public boolean isKeep(TridentTuple tuple) {
			Order order = (Order)tuple.get(0);
			if (order.getCreateDate() == null) return false;
			return true;
		}
	}
	
	public static class AddDateTimeFunction extends BaseFunction{
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Order order = (Order)tuple.get(0);
			String dateStr = DAY_FORMAT.print(order.getCreateDate());
			collector.emit(new Values(dateStr));
		}
		
	}

	public static void main(String[] args) throws InterruptedException{
//		final String host = "www.weather.com.cn";
//		final String protocol= "http";
//		final String kafkaTopic = "order";
//		
//		HttpFetcher fetcher = new HttpFetcher(new URLGenerator() {
//			@Override
//			public URL getUrl() {
//				StringBuilder builder = new StringBuilder("/data/sk/");
//				builder.append("101010100");
//				builder.append(".html");
//				URL url = null;
//				try {
//					url = new URL(protocol, host, builder.toString());
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		});
//		
//		Coordinator<String, Order> coordinator = new Coordinator<String,Order>();
//		coordinator.addFetcher(fetcher);
//		coordinator.addProcessor(new OrderProcessor());
//		coordinator.addSender(new KafkaSender<Order>(kafkaTopic));
//		coordinator.launch();
		
		
		String jsonString = "{\"orderId\":\"C03-0910519-9203246\",\"total\": 100.30,\"createDate\": \"2014-11-12/10:30\","
			    + "\"orderItems\":[{\"goodsId\":\"B00HF32JZE\",\"goodsName\": \"韩寒MOOK2:去你家玩好吗\",\"goodsCategory\": \"图书 > 文学 > 作品集\","
			    + "\"price\":18.30,\"quantity\":1},{\"goodsId\": \"B00119J7N0\",\"goodsName\": \"C++编程思想(第1卷)(第2版)(附光盘)\","
			    + "\"goodsCategory\":\"图书 > 计算机与互联网 > 程序语言与软件开发 > 语言与开发工具 > C语言及其相关\",\"price\":41.00,\"quantity\": 2}]}";
					//JSONObject jsonObject = (JSONObject)JSONValue.parse(jsonString);
//					WeatherInfo info = new WeatherInfo(jsonObject);
//					info.setDateTime(new DateTime());
					
					String jsonString2 = "{\"orderId\":\"C03-0910519-9203245\",\"total\": 100.30,\"createDate\": \"2014-11-12/10:30\","
						    + "\"orderItems\":[{\"goodsId\":\"B00HF32JZE\",\"goodsName\": \"韩寒MOOK2:去你家玩好吗\",\"goodsCategory\": \"图书 > 文学 > 作品集\","
						    + "\"price\":19.30,\"quantity\":2},{\"goodsId\": \"B00119J7N0\",\"goodsName\": \"C++编程思想(第1卷)(第2版)(附光盘)\","
						    + "\"goodsCategory\":\"图书 > 计算机与互联网 > 程序语言与软件开发 > 语言与开发工具 > C语言及其相关\",\"price\":42.00,\"quantity\": 2}]}";
					//JSONObject jsonObject2 = (JSONObject)JSONValue.parse(jsonString2);
//					WeatherInfo info2 = new WeatherInfo(jsonObject2);
//					info2.setDateTime(new DateTime());
					
		FixedBatchSpout batchSpout = new FixedBatchSpout(new Fields(
				"ObjectString"), 2, new Values(jsonString), new Values(
				jsonString2));
		batchSpout.setCycle(true);
		
		CassandraState.Options options = new CassandraState.Options();
		options.columnFamily = "orderbyminute";
		options.clusterName = "Test Cluster";
		options.keyspace = "lambda";
		options.rowKey = "rowKey";
		options.replicationFactor = 1;
		
		
		StateFactory stateFactory = CassandraState.transactional("localhost",options);
		
		StateFactory mapStateFactory = new MemoryMapState.Factory();
		
		//对订单以分钟来统计
		CountByFieldDefinition def = new CountByFieldDefinition("countOrder");
		def.setSpout(batchSpout);
		def.setToObjectFunction(new ToObjectFunction());
		def.setObjectFilter(new ObjectFilter());
		def.addGroupByFieldFunction(new AddDateTimeFunction(), "minute");
		def.setStateFactory(stateFactory);
		
		
		TridentState state = def.creatTridentState();
		String functionName = def.createLocalDPRCQueryFunction(state);
		StormService.submitToLocalServer(def);
		
		for(int i = 0; i < 100; i++) {
			LOG.info(StormService.localStateQuery(functionName,"2014-11-12/10:30"));
			Thread.sleep(5000);
		}
	}
}


