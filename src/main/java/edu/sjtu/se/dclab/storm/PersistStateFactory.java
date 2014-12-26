package edu.sjtu.se.dclab.storm;

import java.util.Map;

import storm.trident.state.StateFactory;
import storm.trident.testing.MemoryMapState;
import trident.cassandra.CassandraState;
import edu.sjtu.se.dclab.util.LambdaConfigUtil;
import edu.sjtu.se.dclab.util.Utils;

public class PersistStateFactory {
	
	private static PersistStateFactory instance;
	private Map<String, Object> config;
	
	public static synchronized PersistStateFactory getPersisteStateFactory(){

		return instance;
	}
	
	private PersistStateFactory(){
		config = Utils.readLambdaConfig();
	}
	
	public StateFactory getMemoryMapState(){
		return new MemoryMapState.Factory();
	}
	
	public StateFactory getCassandraState(StateFactoryOptions stateFactoryOptions ){
		if (!(stateFactoryOptions instanceof CassandraOptions))
			throw new IllegalArgumentException();
		CassandraOptions ops = (CassandraOptions)stateFactoryOptions;
		CassandraState.Options options = new CassandraState.Options();
		options.columnFamily = ops.getColumnFamily();
		options.clusterName = ops.getClusterName();
		options.keyspace = ops.getKeyspace();
		options.rowKey = ops.getRowKey();
		options.replicationFactor = Integer.valueOf(ops.getReplicationFactor());
		StateFactory stateFactory = CassandraState.transactional(
				LambdaConfigUtil.getCassandraHosts(config),options);
		return stateFactory;
	}
	
	public StateFactory getSploutSQLState(){
		return new SploutState.Factory(false, "http://localhost:4412");
	}
}
