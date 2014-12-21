package edu.sjtu.se.dclab.weather;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.entity.WeatherInfo;
import edu.sjtu.se.dclab.util.DateUtil;

public class WeatherProcesser implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(WeatherProcesser.class);
		
	private BlockingQueue<String> jsonInfoQueue;
	private BlockingQueue<WeatherInfo> kafkaWeatherInfoQueue;
	private BlockingQueue<WeatherInfo> hdfsWeatherInfoQueue;
	
	public WeatherProcesser(BlockingQueue<String> jsonInfoQueue, BlockingQueue<WeatherInfo> kafkaWeatherInfoQueue,
			BlockingQueue<WeatherInfo> hdfsWeatherInfoQueue){
		this.jsonInfoQueue = jsonInfoQueue;
		this.kafkaWeatherInfoQueue = kafkaWeatherInfoQueue;
		this.hdfsWeatherInfoQueue = hdfsWeatherInfoQueue;
		
	}
	
	private WeatherInfo process(String json){
		JSONObject obj=(JSONObject) JSONValue.parse(json);
		WeatherInfo info = new WeatherInfo(obj);
		LOG.info("Processing city " + info.getCityId());
		formatDate(info);
		return info;
	}
	
	private void formatDate(WeatherInfo info){
		DateTime newDateTime = null;
		try {
			newDateTime = DateUtil.parse(info.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			newDateTime = new DateTime(); 
		}finally{
			info.setDateTime(newDateTime);
		}
		
	}
	
	@Override
	public void run() {
		while(true){
			try {
				String json = jsonInfoQueue.take();
				WeatherInfo info = process(json);
				if (info == null) continue;
				LOG.info("Put Weather info to Kafka Queue");
				kafkaWeatherInfoQueue.put(info);
				LOG.info("Put Weather info to hdfs Queue");
				hdfsWeatherInfoQueue.put(info);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
