package edu.sjtu.se.dclab.agent;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.util.LambdaConfigUtil;

public class KafkaSender<T2> extends RichSender<T2> {
	private static final Logger LOG = LoggerFactory.getLogger(KafkaSender.class);
	
	private Producer<String,String> producer;
	private String topic;
	
	public KafkaSender(String topic) {
		this.topic = topic;
		
	}
	
	public void init(){
		Properties props = new Properties();
		props.put("metadata.broker.list", LambdaConfigUtil.getKafkaBrokerList(config));
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		
		ProducerConfig producerConfig = new ProducerConfig(props);
		producer = new Producer<String,String>(producerConfig);
	}

	@Override
	public void send(T2 t2) {
		KeyedMessage<String, String> message = 
				new KeyedMessage<String, String>(topic, t2.toString());
		producer.send(message);
		LOG.info("Writing Message to Kafka ...");
	}
	
}
