package edu.sjtu.se.dclab.agent;

public interface Sender<T2> extends Runnable{

	public void init();
	public void send(T2 t);

}
