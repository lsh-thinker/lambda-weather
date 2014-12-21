package edu.sjtu.se.dclab.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.agent.LambdaConfig;
import backtype.storm.Config;


public class LambdaConfigUtil {
	private static final Logger LOG = LoggerFactory.getLogger(LambdaConfigUtil.class);
	
	public static String kafkaBrokerList(Map<String,Object> config){
		String brokerList = String.valueOf(config.get(LambdaConfig.KAFKA_BROKER_LIST));
		return brokerList;
	}
	
}
