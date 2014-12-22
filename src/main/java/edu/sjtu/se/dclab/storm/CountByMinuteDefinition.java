package edu.sjtu.se.dclab.storm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.Filter;
import storm.trident.operation.Function;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;
import storm.trident.state.StateFactory;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.Split;
import storm.trident.tuple.TridentTuple;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import edu.sjtu.se.dclab.entity.WeatherInfo;

public class CountByMinuteDefinition implements Definition{
	
	private TridentTopology topology;
	
	private Function toObjectFunction;
	private Filter objectFilter;
	private Function addDateTimeFunction;
	private String DateTimeFieldsName;
	private StateFactory stateFactory;
	private String topologyName;
	private TridentState tridentState;
	
	public static class CountByDate implements CombinerAggregator<Map<String, Long>> {
		private static final long serialVersionUID = 1L;

		@Override
		public Map<String, Long> init(TridentTuple tuple) {
			Map<String, Long> map = zero();
			map.put(tuple.getString(0), 1L);
			return map;
		}

		@Override
		// map merge
		public Map<String, Long> combine(Map<String, Long> val1, Map<String, Long> val2) {
			for(Map.Entry<String, Long> entry : val2.entrySet()) {
				val2.put(entry.getKey(), MapUtils.getLong(val1, entry.getKey(), 0L) + entry.getValue());
			}
			for(Map.Entry<String, Long> entry : val1.entrySet()) {
				if(val2.containsKey(entry.getKey())) {
					continue;
				}
				val2.put(entry.getKey(), entry.getValue());
			}
			return val2;
		}

		@Override
		// when there is no value it is interesting to return an empty map
		public Map<String, Long> zero() {
			return new HashMap<String, Long>();
		}
	}
	
	public CountByMinuteDefinition(){
		topology = new TridentTopology();
	}
	
	public void setToObjectFunction(Function f){
		this.toObjectFunction = f;
	}
	
	public void setObjectFilter(Filter f){
		this.objectFilter = f;
	}
	
	public void addDateTimeFunction(Function f, String fieldsName){
		this.addDateTimeFunction = f;
		this.DateTimeFieldsName = fieldsName;
	}
	
	public void setStateFactory(StateFactory factory){
		this.stateFactory = factory;
	}
	
	public void buildTopology(String name){
//		SpoutConfig spoutConfig = new SpoutConfig(LambdaConfigUtil.kafkaBrokerList(config), topic, "/kafka", "kafka-consumer");
//		
//		TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(LambdaConfigUtil.kafkaBrokerList(config), "");
//		TransactionalTridentKafkaSpout tKafkaspout = new TransactionalTridentKafkaSpout(tridentKafkaConfig);
		
		
		String jsonString = "{\"orderId\":\"C03-0910519-9203246\",\"total\": 100.30,\"createDate\": \"2014-11-12/10:30\","
    + "\"orderItems\":[{\"goodsId\":\"B00HF32JZE\",\"goodsName\": \"韩寒MOOK2:去你家玩好吗\",\"goodsCategory\": \"图书 > 文学 > 作品集\","
    + "\"price\":18.30,\"quantity\":1},{\"goodsId\": \"B00119J7N0\",\"goodsName\": \"C++编程思想(第1卷)(第2版)(附光盘)\","
    + "\"goodsCategory\":\"图书 > 计算机与互联网 > 程序语言与软件开发 > 语言与开发工具 > C语言及其相关\",\"price\":41.00,\"quantity\": 2}]}";
		//JSONObject jsonObject = (JSONObject)JSONValue.parse(jsonString);
//		WeatherInfo info = new WeatherInfo(jsonObject);
//		info.setDateTime(new DateTime());
		
		String jsonString2 = "{\"orderId\":\"C03-0910519-9203245\",\"total\": 100.30,\"createDate\": \"2014-11-12/10:30\","
			    + "\"orderItems\":[{\"goodsId\":\"B00HF32JZE\",\"goodsName\": \"韩寒MOOK2:去你家玩好吗\",\"goodsCategory\": \"图书 > 文学 > 作品集\","
			    + "\"price\":19.30,\"quantity\":2},{\"goodsId\": \"B00119J7N0\",\"goodsName\": \"C++编程思想(第1卷)(第2版)(附光盘)\","
			    + "\"goodsCategory\":\"图书 > 计算机与互联网 > 程序语言与软件开发 > 语言与开发工具 > C语言及其相关\",\"price\":42.00,\"quantity\": 2}]}";
		//JSONObject jsonObject2 = (JSONObject)JSONValue.parse(jsonString2);
//		WeatherInfo info2 = new WeatherInfo(jsonObject2);
//		info2.setDateTime(new DateTime());
		
		FixedBatchSpout tKafkaspout = new FixedBatchSpout(new Fields("ObjectString"), 2, 
				new Values(jsonString), 
				new Values(jsonString2)
		);
		tKafkaspout.setCycle(true);
		

		
		tridentState = topology
				.newStream("kakfaSpout", tKafkaspout)
				.each(new Fields("ObjectString"), toObjectFunction, new Fields("Object"))
				.each(new Fields("Object"), objectFilter)
				.each(new Fields("Object"), addDateTimeFunction, new Fields(DateTimeFieldsName))
				.groupBy(new Fields(DateTimeFieldsName))
				.persistentAggregate(stateFactory, new Fields(DateTimeFieldsName),
						new CountByDate(), new Fields("Count"));
		
		this.topologyName = name;
	}
	
	public void submit(LocalCluster cluster, LocalDRPC drpcServer){
		topology.newDRPCStream(topologyName+"-drpc", drpcServer)
		.each(new Fields("args"), new Split(), new Fields("DateTime"))
		.stateQuery(tridentState, new Fields("DateTime"), new MapGet(),
				new Fields("Count"))
		.project(new Fields("DateTime", "Count"));
		
		Config conf = new Config();
		conf.setDebug(false);
		cluster.submitTopology(topologyName, conf, topology.build());
	}

	
	
}
