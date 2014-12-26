package edu.sjtu.se.dclab.storm;

import storm.trident.state.StateType;

public class CassandraOptions extends StateFactoryOptions {
	
	private static final String COLUMN_FAMILY = "COLUMN_FAMILY";
	private static final String CLUSTER_NAME = "CLUSTER_NAME";
	private static final String ROW_KEY = "ROW_KEY";
	private static final String KEYSPACE = "KEYSPACE";
	private static final String REPLICATION_FACTOR = "REPLICATION_FACTOR";
	
	private static final String STATE_TYPE = "STATE_TYPE";
	
	public String getColumnFamily() {
		return getOptionMap().get(COLUMN_FAMILY);
	}
	public void setColumnFamily(String columnFamily) {
		getOptionMap().put(COLUMN_FAMILY, columnFamily);
	}
	
	public String getKeyspace() {
		return getOptionMap().get(KEYSPACE);
	}
	public void setKeyspace(String keyspace) {
		getOptionMap().put(KEYSPACE,  keyspace);
	}
	public String getRowKey() {
		return getOptionMap().get(ROW_KEY);
	}
	public void setRowKey(String rowKey) {
		getOptionMap().put(ROW_KEY,  rowKey);
	}
	public String getReplicationFactor() {
		return getOptionMap().get(REPLICATION_FACTOR);
	}
	public void setReplicationFactor(String replicationFactor) {
		getOptionMap().put(REPLICATION_FACTOR,  replicationFactor);
	}
	public String getClusterName() {
		return getOptionMap().get(CLUSTER_NAME);
	}
	public void setClusterName(String clusterName) {
		getOptionMap().put(CLUSTER_NAME,clusterName);
	}
	
	public void setStateType(StateType st){
		getOptionMap().put(STATE_TYPE, st.name());
	}
	
	public StateType getStateType(){
		return StateType.valueOf(getOptionMap().get(STATE_TYPE));
	}

}
