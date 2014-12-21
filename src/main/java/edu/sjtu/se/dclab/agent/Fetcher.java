package edu.sjtu.se.dclab.agent;

public interface Fetcher<T> extends Runnable{
	public void init();
	public T fetch();
}
