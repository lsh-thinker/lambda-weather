package edu.sjtu.se.dclab.storm;

import backtype.storm.topology.TopologyBuilder;

public abstract class TopologyDefinition extends Definition{
	

	private TopologyBuilder builder;
	
	public TopologyDefinition(String topologyName){
		super(topologyName);
		builder = new TopologyBuilder();
	}
	
	public TopologyBuilder getBuilder(){
		return builder;
	}
	

	
}
