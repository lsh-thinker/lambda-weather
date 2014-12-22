package edu.sjtu.se.dclab.agent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.entity.City;
import edu.sjtu.se.dclab.entity.WeatherInfo;
import edu.sjtu.se.dclab.weather.CityLoader;
import edu.sjtu.se.dclab.weather.FileCityLoader;
import edu.sjtu.se.dclab.weather.KafkaWeatherSender;
import edu.sjtu.se.dclab.weather.WeatherFetcher;

public class CopyOfCoordinator<T1, T2> {
	
	public static final String HOST = "www.weather.com.cn";
	public static final String PROTOCAL = "http";
	public static final String TOPIC = "weather";
	
	public static final Logger LOG = LoggerFactory.getLogger(CopyOfCoordinator.class);

	public static final String cityFilePath = "citys.txt";
	
	static final ScheduledExecutorService scheduleService =  Executors.newScheduledThreadPool(3);;

	private String brokerList;
	
	private List<Fetcher<T1>> fetcherList;
	private List<Processor<T1,T2>> processorList;
	private List<Sender<T2>> senderList;
	

	public CopyOfCoordinator() {
		brokerList = "127.0.0.1:9092";
		
		fetcherList = new ArrayList<Fetcher<T1>>();
		processorList = new ArrayList<Processor<T1, T2>>();
		senderList = new ArrayList<Sender<T2>>();
	}
	
	private void init(){
		URL url = CopyOfCoordinator.class.getClassLoader().getResource(cityFilePath);
		if (url == null) {
			throw new RuntimeException("City file not found!");
		}
		LOG.info("City file path:" + url.getPath());
		
		CityLoader loader = new FileCityLoader(url.getPath());
		Map<String, City> citys = loader.load();
		
		
	}
	
	private void launch() {
		init();
		
		
	}

	public void service() {
		
		BlockingQueue<String> jsonQueue = new LinkedBlockingQueue<String>();
		BlockingQueue<WeatherInfo> kafkaWeatherInfoQueue = new LinkedBlockingQueue<WeatherInfo>(); 
//		BlockingQueue<WeatherInfo> hdfsWeatherInfoQueue = new LinkedBlockingQueue<WeatherInfo>(); 
		
		
		scheduleService.scheduleAtFixedRate(
				new WeatherFetcher(HOST, PROTOCAL, jsonQueue), 10, 10, TimeUnit.SECONDS);
		
		ExecutorService executorService = Executors.newFixedThreadPool(3);
//		executorService.execute(new WeatherProcesser(jsonQueue, kafkaWeatherInfoQueue, 
//				hdfsWeatherInfoQueue));
		executorService.execute(new KafkaWeatherSender(TOPIC, brokerList, kafkaWeatherInfoQueue));
//		executorService.execute(new FileWeatherSender("/home/thinker/workspace/", hdfsWeatherInfoQueue, 2, TimeUnit.MINUTES));
		//whetherFetcher = new WeatherFetcher("www.weather.com.cn", "http");
		//whetherFetcher.getWhetherInfos(new ArrayList<String>(citys.keySet()));
	}
	
	public void addFetcher(Fetcher<T1> fetcher){
		fetcherList.add(fetcher);
	}
	
	public void addProcessor(Processor<T1,T2> processor){
		processorList.add(processor);
	}
	
	public void addSender(Sender<T2> sender){
		senderList.add(sender);
	}

	public static void main(String[] args) {
		new CopyOfCoordinator().launch();
	}

}
