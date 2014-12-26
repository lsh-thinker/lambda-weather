package edu.sjtu.se.dclab.storm;

import java.util.HashMap;
import java.util.Map;

public abstract class SpoutOptions {
	private Map<String, String> optionMap;
	
	public SpoutOptions(){
		optionMap = new HashMap<String, String>();
	}
	
	protected Map<String, String> getOptionMap(){
		return optionMap;
	}
}
