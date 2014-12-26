package edu.sjtu.se.dclab.storm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;

import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.operation.CombinerAggregator;
import storm.trident.operation.Filter;
import storm.trident.operation.Function;
import storm.trident.operation.builtin.MapGet;
import storm.trident.spout.IBatchSpout;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import storm.trident.spout.IPartitionedTridentSpout;
import storm.trident.spout.ITridentSpout;
import storm.trident.state.StateFactory;
import storm.trident.testing.Split;
import storm.trident.tuple.TridentTuple;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.tuple.Fields;

public class CountByFieldDefinition extends TridentTopologyDefinition{
	
	private Function toObjectFunction;
	private Filter objectFilter;
	private Function addGroupByFieldFunction;
	private String groupByField;
	private StateFactory stateFactory;
	private int parallelism;
	private Object spout;
	
	
	public void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}

	public static class CountByDate implements CombinerAggregator<Map<String, Long>> {
		private static final long serialVersionUID = 1L;

		@Override
		public Map<String, Long> init(TridentTuple tuple) {
			Map<String, Long> map = zero();
			map.put(tuple.getString(0), 1L);
			return map;
		}

		@Override
		// map merge
		public Map<String, Long> combine(Map<String, Long> val1, Map<String, Long> val2) {
			for(Map.Entry<String, Long> entry : val2.entrySet()) {
				val2.put(entry.getKey(), MapUtils.getLong(val1, entry.getKey(), 0L) + entry.getValue());
			}
			for(Map.Entry<String, Long> entry : val1.entrySet()) {
				if(val2.containsKey(entry.getKey())) {
					continue;
				}
				val2.put(entry.getKey(), entry.getValue());
			}
			return val2;
		}

		@Override
		// when there is no value it is interesting to return an empty map
		public Map<String, Long> zero() {
			return new HashMap<String, Long>();
		}
	}
	
	public void setSpout(Object spout) {
		this.spout = spout;
	}

	public CountByFieldDefinition(String name){
		super(name);
		parallelism = 1;
	}
	
	public void setToObjectFunction(Function f){
		this.toObjectFunction = f;
	}
	
	public void setObjectFilter(Filter f){
		this.objectFilter = f;
	}
	
	public void addGroupByFieldFunction(Function f, String fieldsName){
		this.addGroupByFieldFunction = f;
		this.groupByField = fieldsName;
	}
	
	public void setStateFactory(StateFactory factory){
		this.stateFactory = factory;
	}
	
	private void checkNull(){
		if (spout == null) 
			throw new NullPointerException("Spout is null");
		if (toObjectFunction == null) 
			throw new NullPointerException("toObjectFunction is null");
		if (objectFilter == null) 
			throw new NullPointerException("objectFilter is null");
		if (addGroupByFieldFunction == null) 
			throw new NullPointerException("addGroupByFieldFunction is null");
		if (stateFactory == null) 
			throw new NullPointerException("stateFactory is null");
	}
	
	public TridentState creatTridentState(){
		checkNull();
		Stream stream = null;
		if (spout instanceof IRichSpout){
			stream = getTridentTopology().newStream(
					UUID.randomUUID().toString(), (IRichSpout)spout);
		}else if (spout instanceof IPartitionedTridentSpout){
			stream = getTridentTopology().newStream(
					UUID.randomUUID().toString(), (IPartitionedTridentSpout)spout);
		}else if (spout instanceof IBatchSpout){
			stream = getTridentTopology().newStream(
					UUID.randomUUID().toString(), (IBatchSpout)spout);
		}else if (spout instanceof IOpaquePartitionedTridentSpout){
			stream = getTridentTopology().newStream(
					UUID.randomUUID().toString(), (IOpaquePartitionedTridentSpout)spout);
		}else if (spout instanceof ITridentSpout){
			stream = getTridentTopology().newStream(
					UUID.randomUUID().toString(), (ITridentSpout)spout);
		}else{
			throw new IllegalArgumentException("Spout type error");
		}
			
		TridentState tridentState = stream
		.each(new Fields("ObjectString"), toObjectFunction, new Fields("Object"))
		.each(new Fields("Object"), objectFilter)
		.each(new Fields("Object"), addGroupByFieldFunction, new Fields(groupByField))
		.groupBy(new Fields(groupByField))
		.persistentAggregate(stateFactory, new Fields(groupByField),
				new CountByDate(), new Fields("Count")).parallelismHint(parallelism);
		
		return tridentState;
	}
	
	@Override
	public StormTopology buildTopology(){
		return getTridentTopology().build();
	}
	
	public String createDPRCQueryFunction(TridentState tridentState){
		String functionName = getName() + "-drpc";
		if (tridentState == null) throw new NullPointerException("You must create the trident state first");
		getTridentTopology().newDRPCStream(functionName)
		.each(new Fields("args"), new Split(), new Fields("DateTime"))
		.stateQuery(tridentState, new Fields("DateTime"), new MapGet(),
				new Fields("Count"))
		.project(new Fields("DateTime", "Count"));
		return functionName;
	}
	
	public String createLocalDPRCQueryFunction(TridentState tridentState){
		String functionName = getName() + "-drpc";
		if (tridentState == null) throw new NullPointerException("You must create the trident state first");
		getTridentTopology().newDRPCStream(functionName,  StormService.drpc)
		.each(new Fields("args"), new Split(), new Fields("arg"))
		.stateQuery(tridentState, new Fields("arg"), new MapGet(),
				new Fields("Count"))
		.project(new Fields("arg", "Count"));
		return functionName;
	}
	
}
