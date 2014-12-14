package edu.sjtu.se.dclab.storm;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.MapGet;
import storm.trident.state.StateFactory;
import storm.trident.testing.FixedBatchSpout;
import storm.trident.testing.MemoryMapState;
import storm.trident.tuple.TridentTuple;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.splout.db.qnode.beans.QueryStatus;

import edu.sjtu.se.dclab.entity.WeatherInfo;

public class LambdaWeatherTopology {
	
	private static final Logger LOG = LoggerFactory.getLogger(LambdaWeatherTopology.class);
	
	private final static DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd/HH:mm");
	
	private static final String[] WIND_DIRECTIONS = {"东风","西风","西南风"};
	
	public static class WeatherFilter extends BaseFunction {

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String word = tuple.getString(0);
			if(word.startsWith("#")) {
				// only emit hashtags, and emit them without the # character
				collector.emit(new Values(word.substring(1, word.length())));
			}
		}
	}
	
	public static class WindDirectionSpliter extends BaseFunction{
		
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			for(String windDirection: WIND_DIRECTIONS){
				collector.emit(new Values(tuple.get(0), windDirection));
			}
		}
		
	}
	
	public static class WindDirectionFunction extends BaseFunction{

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			WeatherInfo info = (WeatherInfo)tuple.get(0);
//			LOG.info("Processing Weather info: " + info.getCity());
			collector.emit(new Values(info.getWD(),info.getCity(), DAY_FORMAT.print(new DateTime())));
		}
		
	}
	
	public static class CountByCity implements CombinerAggregator<Set<String>> {

		@Override
		public Set<String> init(TridentTuple tuple) {
			Set<String> set = zero();
			set.add(tuple.getString(0));
			return set;
		}

		@Override
		// map merge
		public Set<String> combine(Set<String> s1, Set<String> s2) {
			for(String str: s1) {
				if(s2.contains(str)) {
					continue;
				}
				s2.add(str);
			}
			return s2;
		}

		@Override
		// when there is no value it is interesting to return an empty map
		public Set<String> zero() {
			return new HashSet<String>();
		}
	}
	
	public static class CountByDate implements CombinerAggregator<Map<String, Long>> {
		private static final long serialVersionUID = 1L;

		@Override
		public Map<String, Long> init(TridentTuple tuple) {
			Map<String, Long> map = zero();
			map.put(tuple.getString(1), 1L);
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
	
	public static class One implements CombinerAggregator<Integer> {
		   public Integer init(TridentTuple tuple) {
		       return 1;
		   }

		   public Integer combine(Integer val1, Integer val2) {
		       return 1;
		   }

		   public Integer zero() {
		       return 1;
		   }        
		}
	
	public static class LambdaMerge extends BaseFunction {

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			Map<String, Long> resultRealTime = (Map<String, Long>) tuple.get(1);
			QueryStatus resultBatch = (QueryStatus) tuple.get(2);

			TreeMap<String, Long> consolidatedResult;

			if(resultRealTime != null) {
				consolidatedResult = new TreeMap<String, Long>(resultRealTime);
			} else {
				consolidatedResult = new TreeMap<String, Long>();
			}

			if(resultBatch != null) {
				if(resultBatch.getResult() != null) {
					for(Object rowBatch : resultBatch.getResult()) {
						Map<String, Object> mapRow = (Map<String, Object>) rowBatch;
						String day = (String) mapRow.get("day");
						// we do this since Splout may return Integer or Long depending on the value
						Long count = Long.parseLong(mapRow.get("SUM(count)").toString());
						// In the real-time map we set the values from the batch view
						// Therefore if there is a value coming from batch it will override any value from real-time
						// This is the usual decision for lambda architectures.
						consolidatedResult.put(day, count);
					}
				}
			}

			collector.emit(new Values(consolidatedResult));
		}
	}

	
	
	public static StormTopology buildTopology(LocalDRPC drpc) {

		TridentTopology topology = new TridentTopology();

		String now = DAY_FORMAT.print(new Date().getTime());
		
//		TransactionalTridentKafkaSpout kafkaSpout = 
		
		// This is just a dummy cyclic spout that only emits two tweets
//		FixedBatchSpout spout = new FixedBatchSpout(new Fields("weatherInfo", "date"), 3, 
//				new Values("#california is cool", now), 
//				new Values("I like #california", now)
//		);
		String jsonString = "{\"weatherinfo\":{\"city\":\"潮州\",\"cityid\":\"101281501\",\"temp\":\"14\",\"WD\":\"西风\","
				+ "\"WS\":\"2级\",\"SD\":\"40%\",\"WSE\":\"2\",\"time\":\"10:30\",\"isRadar\":\"0\",\"Radar\":\"\",\"njd\":\"暂无实况\",\"qy\":\"1021\"}}";
		JSONObject jsonObject = (JSONObject)JSONValue.parse(jsonString);
		WeatherInfo info = new WeatherInfo(jsonObject);
		info.setDateTime(new DateTime());
		
		String jsonString2 = "{\"weatherinfo\":{\"city\":\"北京\",\"cityid\":\"101010100\",\"temp\":\"1\",\"WD\":\"西南风\",\"WS\":\"3级\",\"SD\":\"22%\","
				+ "\"WSE\":\"3\",\"time\":\"10:35\",\"isRadar\":\"1\",\"Radar\":\"JC_RADAR_AZ9010_JB\",\"njd\":\"暂无实况\",\"qy\":\"1027\"}}";
		JSONObject jsonObject2 = (JSONObject)JSONValue.parse(jsonString2);
		WeatherInfo info2 = new WeatherInfo(jsonObject2);
		info2.setDateTime(new DateTime());
		
		FixedBatchSpout spout = new FixedBatchSpout(new Fields("WeatherInfo"), 3, 
				new Values(info), 
				new Values(info2)
		);
		spout.setCycle(true);

		// In this state we will save the real-time counts per minute for each wind direction in each area
		StateFactory mapState = new MemoryMapState.Factory();

		// Real-time part of the system: a Trident topology that groups by wind direction and stores count
		TridentState directionCounts = topology
		    .newStream("weatherSpout", spout)
		    // note how we carry the date around
		    //.each(new Fields("tweet", "date"), new Split(), new Fields("word"))
		    //.each(new Fields("word", "date"), new WeatherFilter(), new Fields("windDirection"))
		    .each(new Fields("WeatherInfo"), new WindDirectionFunction(), new Fields("WindDirection", "City", "DateTime"))
		    //以时间和风向为维度进行统计
		    .groupBy(new Fields("DateTime","WindDirection"))
		    //这里的第一个字段已经不是info了，0是Windrection，1是城市
		    .persistentAggregate(mapState, new Fields("City"), new CountByCity(), new Fields("CityList"));

		// Batch part of the system:
		// We instantiate a Splout connector that doesn't fail fast so we can work without the batch layer.
		// This TridentState can be used to query Splout.
//		TridentState sploutState = topology.newStaticState(new SploutState.Factory(false,
//		    "http://localhost:4412"));

		// DRPC service:
		// Accepts a "hashtag" argument and queries first the real-time view and then the batch-view. Finally,
		// it uses a custom Function "LambdaMerge" for merging the results and projects the results back to the user.
		topology
		    .newDRPCStream("QueryWindDirection", drpc)
		    //查询八个方向的风，以时间为维度进行统计
		    .each(new Fields("args"), new WindDirectionSpliter(), new Fields("DateTime", "WindDirection"))
		    //.each(new Fields("args"), new ArgsSpliter(), new Fields("DateTime", "City"))
		    //groupby的方式决定了用什么字段查询
//		    .groupBy(new Fields("DateTime", "City"))
		    .stateQuery(directionCounts, new Fields("DateTime","WindDirection"), new MapGet(), new Fields("WindDirectionCount"))
		    //.stateQuery(sploutState, new Fields("hashtag", "resultrt"), new WeatherSploutQuery(), new Fields("resultbatch"))
		    //.each(new Fields("hashtag", "resultrt", "resultbatch"), new LambdaMerge(), new Fields("result"))
		    // Project allows us to keep only the interesting results
		    .project(new Fields("DateTime","WindDirection","WindDirectionCount"));

		return topology.build();
	}
	
	
	public static void main(String[] args) throws Exception {
		Config conf = new Config();
		conf.setMaxSpoutPending(20);
		conf.setDebug(false);

		// This topology can only be run as local because it is a toy example
		LocalDRPC drpc = new LocalDRPC();
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("hashtagCounter", conf, buildTopology(drpc));
		// Query 100 times for hashtag "california" for illustrating the effect of the lambda architecture
		for(int i = 0; i < 100; i++) {
			LOG.info("-------------------------------------------------------------------------------");
			//LOG.info("Result for WindDirection '西风、东风' -> " + drpc.execute("windDirectionCount", DAY_FORMAT.print(new Date().getTime()) + ",潮州 " + DAY_FORMAT.print(new DateTime().plusMinutes(1)) + ",潮州"));
			LOG.info(drpc.execute("QueryWindDirection", DAY_FORMAT.print(new DateTime())));
			Thread.sleep(5000);
		}
	}

}
