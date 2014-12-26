package edu.sjtu.se.dclab.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.agent.LambdaConfig;

public class LambdaConfigUtil {
	private static final Logger LOG = LoggerFactory.getLogger(LambdaConfigUtil.class);
	
	public static void setKafkaBrokerList(Map<String, Object> config, String brokerList){
		config.put(LambdaConfig.KAFKA_BROKER_LIST, brokerList);
	}
	
	public static String getKafkaBrokerList(Map<String, Object> config){
		return String.valueOf(config.get(LambdaConfig.KAFKA_BROKER_LIST));
	}
	
	public static void setKafkaZookeeperRoot(Map<String, Object> config, String kakfaZookeeperRoot){
		config.put(LambdaConfig.KAFKA_ZOOKEEPER_ROOT, kakfaZookeeperRoot);
	}
	
	public static String getKafkaZookeeperRoot(Map<String, Object> config){
		return String.valueOf(config.get(LambdaConfig.KAFKA_ZOOKEEPER_ROOT));
	}
	
}
