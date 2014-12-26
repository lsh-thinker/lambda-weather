package edu.sjtu.se.dclab.storm;

import storm.trident.Stream;

public interface StreamFactory {
	public Stream getStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options);
	public Stream getOpaqueStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options);
	public Stream getTransactionalStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options);
	public Stream getBatchStream(TridentTopologyDefinition tridentTopologyDefinition, SpoutOptions options);
}
