package edu.sjtu.se.dclab.agent;

import edu.sjtu.se.dclab.entity.WeatherInfo;



public interface WeatherSender {
	
	public void send(WeatherInfo info);
}
