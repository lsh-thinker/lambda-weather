package edu.sjtu.se.dclab.storm;

import storm.trident.spout.IBatchSpout;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import storm.trident.spout.IPartitionedTridentSpout;
import backtype.storm.topology.IRichSpout;

public interface SpoutFactory {
	public IRichSpout getRichSpout(SpoutOptions options);
	public IBatchSpout getBatchSpout(SpoutOptions options);
	public IOpaquePartitionedTridentSpout getOpaqueTridentSpout(SpoutOptions options);
	public IPartitionedTridentSpout getTransactionalTridentSpout(SpoutOptions options);
}
