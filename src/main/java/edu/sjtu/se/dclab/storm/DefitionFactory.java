package edu.sjtu.se.dclab.storm;

import storm.trident.TridentState;


public class DefitionFactory {
	
	public static CountByMinuteDefinition getCountByMinuteDefinition(){
		return new CountByMinuteDefinition();
	}
	
	public static void getCountByHourTopology(){
		
	}
	
	public static void getTransactionalKafkaStream(String streamName){
		
	}
}
