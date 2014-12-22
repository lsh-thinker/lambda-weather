package edu.sjtu.se.dclab.storm;

import storm.trident.Stream;
import storm.trident.TridentTopology;

public abstract class StreamDefinition implements Definition{
	
	protected TridentTopology topology;

	@Override
	public void buildTopology(String name) {
		
	}
	
//	public abstract Stream getStream(String streamName);
	
	
}
