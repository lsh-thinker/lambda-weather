package edu.sjtu.se.dclab.storm;

import java.util.HashMap;
import java.util.Map;

public abstract class StateFactoryOptions {
	private Map<String, String> optionMap;
	
	public StateFactoryOptions(){
		optionMap = new HashMap<String, String>();
	}
	
	protected Map<String, String> getOptionMap(){
		return optionMap;
	}
}
