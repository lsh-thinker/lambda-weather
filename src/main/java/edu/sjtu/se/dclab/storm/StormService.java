package edu.sjtu.se.dclab.storm;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;

public final class StormService {
	
	static final LocalCluster localCluster = new LocalCluster();
	static final LocalDRPC drpc = new LocalDRPC();
	
	
	public static void submitToLocalServer(TridentTopologyDefinition tridentTopologyDefinition){
		localCluster.submitTopology(tridentTopologyDefinition.getName(), 
				tridentTopologyDefinition.getConfig(), tridentTopologyDefinition.buildTopology());
//		localCluster.submitTopology(, arg1, arg2);
	}
	
	public static void submitToRemoteServer(TridentTopologyDefinition tridentTopologyDefinition) throws AlreadyAliveException, InvalidTopologyException{
		Config conf = new Config();
		conf.setDebug(false);
		StormSubmitter.submitTopology(tridentTopologyDefinition.getName(), 
				tridentTopologyDefinition.getConfig(), 
				tridentTopologyDefinition.buildTopology());
	}
	
	public static String localStateQuery(String function, String args){
		return drpc.execute(function, args);
	}
}
