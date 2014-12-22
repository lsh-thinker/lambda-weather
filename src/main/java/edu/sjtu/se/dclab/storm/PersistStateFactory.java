package edu.sjtu.se.dclab.storm;

import storm.trident.state.StateFactory;
import storm.trident.testing.MemoryMapState;
import trident.cassandra.CassandraState;

public class PersistStateFactory {
	
	public StateFactory getMemoryMapState(){
		return new MemoryMapState.Factory();
	}
	
	public StateFactory getCassandraState(){
		CassandraState.Options options = new CassandraState.Options();
		options.columnFamily = "orderbyminute";
		options.clusterName = "Test Cluster";
		options.keyspace = "lambda";
		options.rowKey = "rowKey";
		options.replicationFactor = 1;
		StateFactory stateFactory = CassandraState.transactional("localhost",options);
		return stateFactory;
	}
	
	public StateFactory getSploutSQLState(){
		return new SploutState.Factory(false, "http://localhost:4412");
	}
}
