package edu.sjtu.se.dclab.storm;

import java.util.UUID;

import storm.trident.Stream;

public class KafkaStreamFactory implements StreamFactory{

	public Stream getStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options){
		KafkaSpoutFactory factory = KafkaSpoutFactory.newFactory();
		return tridentTopologyDefinition.getTridentTopology()
				.newStream(UUID.randomUUID().toString(), factory.getRichSpout(options));
	}
	
	public Stream getOpaqueStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options){
		KafkaSpoutFactory factory = KafkaSpoutFactory.newFactory();
		return tridentTopologyDefinition.getTridentTopology()
				.newStream(UUID.randomUUID().toString(), factory.getOpaqueTridentSpout(options));
	}
	
	
	
	public Stream getTransactionalStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options){
		KafkaSpoutFactory factory = KafkaSpoutFactory.newFactory();
		return tridentTopologyDefinition.getTridentTopology()
				.newStream(UUID.randomUUID().toString(), factory.getTransactionalTridentSpout(options));
	}

	@Override
	public Stream getBatchStream(
			TridentTopologyDefinition tridentTopologyDefinition,
			SpoutOptions options) {
		throw new UnsupportedOperationException("Unsupported Opertion...");
	}
	
	
}
