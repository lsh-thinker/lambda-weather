package edu.sjtu.se.dclab.agent;

public interface Processor<T1,T2> extends Runnable{
	
	public void init();
	public T2 process(T1 t1);
}
