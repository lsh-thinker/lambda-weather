package edu.sjtu.se.dclab.agent;

import java.io.Serializable;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFilter;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.state.Serializer;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import trident.cassandra.CassandraState;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.tuple.Values;
import edu.sjtu.se.dclab.entity.Order;
import edu.sjtu.se.dclab.storm.CountByMinuteDefinition;
import edu.sjtu.se.dclab.storm.DefitionFactory;

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
		
		CassandraState.Options options = new CassandraState.Options();
		options.columnFamily = "orderbyminute";
		options.clusterName = "Test Cluster";
		options.keyspace = "lambda";
		options.rowKey = "rowKey";
		options.replicationFactor = 1;
		
		
		StateFactory stateFactory = CassandraState.transactional("localhost",options);
		
		LocalCluster cluster= new LocalCluster();
		LocalDRPC drpc = new LocalDRPC();
		//对订单以分钟来统计
		CountByMinuteDefinition def = DefitionFactory.getCountByMinuteDefinition();
		def.setToObjectFunction(new ToObjectFunction());
		def.setObjectFilter(new ObjectFilter());
		def.addDateTimeFunction(new AddDateTimeFunction(), "minute");
//		def.setStateFactory(new MemoryMapState.Factory());
		def.setStateFactory(stateFactory);
		def.buildTopology("countOrder");
		def.submit(cluster,drpc);
		
		for(int i = 0; i < 100; i++) {
			LOG.info(drpc.execute("countOrder-drpc","2014-11-12/10:30"));
			Thread.sleep(5000);
		}
		
	}
}


