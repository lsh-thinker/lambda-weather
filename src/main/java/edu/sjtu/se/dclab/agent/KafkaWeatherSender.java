package edu.sjtu.se.dclab.agent;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.entity.WeatherInfo;

public class KafkaWeatherSender implements WeatherSender, Runnable{
	private static final Logger LOG = LoggerFactory.getLogger(KafkaWeatherSender.class);
	
	private BlockingQueue<WeatherInfo> weatherInfoQueue;
	private Producer<String,String> producer;
	private String topic;
	
	public KafkaWeatherSender(String topic, String brokerList, BlockingQueue<WeatherInfo> weatherInfoQueue){
		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
//		props.put("serializer.class", "edu.sjtu.se.dclab.weather.sender.WeatherSerializer");
//		props.put("serializer.class", "kafka.serializer.DefaultEncoder");
		props.put("request.required.acks", "1");
		
		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String,String>(config);
		this.topic = topic;
		this.weatherInfoQueue = weatherInfoQueue;
	}

	@Override
	public void run() {
		while(true){
			try {
				WeatherInfo info = weatherInfoQueue.take();
				send(info);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void send(WeatherInfo info) {
		KeyedMessage<String, String> message = 
				new KeyedMessage<String, String>(topic, info.toString());
//		ProducerData<String, WeatherInfo> data = 
//				new ProducerData<String, String>(topic, info);
		producer.send(message);
		LOG.info("Writing Message to Kafka ...");
	}	
	
}









