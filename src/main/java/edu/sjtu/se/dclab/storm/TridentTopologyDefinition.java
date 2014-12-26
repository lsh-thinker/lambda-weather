package edu.sjtu.se.dclab.storm;

import storm.trident.TridentTopology;

public abstract class TridentTopologyDefinition extends Definition {
	
	private TridentTopology tridentTopology;
	
	
	public TridentTopologyDefinition(String name){
		super(name);
		tridentTopology = new TridentTopology();
	}
	
	protected TridentTopology getTridentTopology(){
		return tridentTopology;
	}
	
	
	
}
