package edu.sjtu.se.dclab.weather;

import edu.sjtu.se.dclab.entity.WeatherInfo;



public interface WeatherSender {
	
	public void send(WeatherInfo info);
}
