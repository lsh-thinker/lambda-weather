package edu.sjtu.se.dclab.agent;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.rolling.helper.PeriodicityType;
import edu.sjtu.se.dclab.entity.City;
import edu.sjtu.se.dclab.entity.WeatherInfo;

public class Coordinator {
	
	public static final String HOST = "www.weather.com.cn";
	public static final String PROTOCAL = "http";
	public static final String TOPIC = "weather";
	
	public static final Logger LOG = LoggerFactory.getLogger(Coordinator.class);

	public static final String cityFilePath = "citys.txt";
	
	static final ScheduledExecutorService scheduleService =  Executors.newScheduledThreadPool(3);;

	private String brokerList;
	

	public Coordinator() {
		brokerList = "127.0.0.1:9092";
	}
	
	private void init(){
		
	}

	public void service() {
		URL url = Coordinator.class.getClassLoader().getResource(cityFilePath);
		if (url == null) {
			throw new RuntimeException("City file not found!");
		}
		LOG.info("City file path:" + url.getPath());
		
		CityLoader loader = new FileCityLoader(url.getPath());
		Map<String, City> citys = loader.load();
		
		BlockingQueue<String> jsonQueue = new LinkedBlockingQueue<String>();
		BlockingQueue<WeatherInfo> kafkaWeatherInfoQueue = new LinkedBlockingQueue<WeatherInfo>(); 
		BlockingQueue<WeatherInfo> hdfsWeatherInfoQueue = new LinkedBlockingQueue<WeatherInfo>(); 
		
		
		scheduleService.scheduleAtFixedRate(
				new WeatherFetcher(HOST, PROTOCAL, jsonQueue), 10, 10, TimeUnit.SECONDS);
		
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		executorService.execute(new WeatherProcesser(jsonQueue, kafkaWeatherInfoQueue, 
				hdfsWeatherInfoQueue));
		//executorService.execute(new KafkaWeatherSender(TOPIC, brokerList, kafkaWeatherInfoQueue));
		executorService.execute(new FileWeatherSender("/home/thinker/workspace/", hdfsWeatherInfoQueue, 2, TimeUnit.MINUTES));
		

		//whetherFetcher = new WeatherFetcher("www.weather.com.cn", "http");
		//whetherFetcher.getWhetherInfos(new ArrayList<String>(citys.keySet()));
	}

	public static void main(String[] args) {

		new Coordinator().service();

	}

}
