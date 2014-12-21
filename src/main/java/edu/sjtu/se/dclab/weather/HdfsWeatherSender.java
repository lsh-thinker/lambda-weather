package edu.sjtu.se.dclab.weather;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.entity.WeatherInfo;

public class HdfsWeatherSender implements  WeatherSender, Runnable{
	private static final Logger LOG = LoggerFactory.getLogger(HdfsWeatherSender.class);
	
	private BlockingQueue<WeatherInfo> weatherInfoQueue;
	
	public HdfsWeatherSender(BlockingQueue<WeatherInfo> weatherInfoQueue){
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
		
	}

}
