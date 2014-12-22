package edu.sjtu.se.dclab.storm;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.Stream;
import edu.sjtu.se.dclab.util.LambdaConfigUtil;

public class KafkaStreamDefinition extends StreamDefinition{

	public Stream getStream(String streamName, String topic){
		SpoutConfig spoutConfig = new SpoutConfig(LambdaConfigUtil.kafkaBrokerList(config), topic, "/kafka", "kafka-consumer");
		KafkaSpout spout = new KafkaSpout(spoutConfig);
		topology.newStream(streamName, spout);
	}
	
	public Stream getTransactionalStream(String streamName, String topic){
		TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(LambdaConfigUtil.kafkaBrokerList(config), "");
		TransactionalTridentKafkaSpout tKafkaspout = new TransactionalTridentKafkaSpout(tridentKafkaConfig);
		topology.newStream(streamName, tKafkaspout);
	}
	
	
}
