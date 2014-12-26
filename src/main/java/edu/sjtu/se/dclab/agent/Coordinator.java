package edu.sjtu.se.dclab.agent;

public interface Coordinator<T1, T2> {
	
	public void launch();

	public void addFetcher(RichFetcher<T1> fetcher);

	public void addProcessor(RichProcessor<T1, T2> processor);

	public void addSender(RichSender<T2> sender);
}
