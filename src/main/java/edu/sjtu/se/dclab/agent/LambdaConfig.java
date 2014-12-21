package edu.sjtu.se.dclab.agent;

import java.util.HashMap;
import java.util.Map;

public class LambdaConfig extends HashMap<String, Object> {
	
	public static final String KAFKA_BROKER_LIST = "kafka.broker.list";
	public static final Object KAFKA_BROKER_LIST_SCHEMA = String.class;
//	public static final String STORM_TOPOLOGY_HISTORY_SCHEDULE = "storm.topology.hisitory.schedule";
//	public static final Object STORM_TOPOLOGY_HISTORY_SCHEDULE_SCHEMA = String.class;
//	public static final String STORM_COMPONENT_RESOURCE_TYPE = "storm.component.resource.type";
//	public static final Object STORM_COMPONENT_RESOURCE_TYPE_SCHEMA = String.class;
	
	public void setKafkaBrokerList(Map<String, Object> config, String brokerList){
		config.put(KAFKA_BROKER_LIST, brokerList);
	}
}
