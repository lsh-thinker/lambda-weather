package edu.sjtu.se.dclab.storm;

public class KafkaSpoutOptions extends SpoutOptions {
	
	private static final String KAFKA_TOPIC = "KAKFA_TOPIC";
	
	public void setTopic(String topic){
		getOptionMap().put(KAFKA_TOPIC, topic);
	}
	
	public String getTopic(){
		return getOptionMap().get(KAFKA_TOPIC);
	}
}
