package edu.sjtu.se.dclab.storm;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;

public abstract class Definition {
	
	private String topologyName;
	private Config config;
	
	public Definition(String name){
		this.topologyName = name;
		config = new Config();
	}
	
	public String getName(){
		return topologyName;
	}
	
	public abstract StormTopology buildTopology();

	public void setConfig(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}
	
	
	
	
}
