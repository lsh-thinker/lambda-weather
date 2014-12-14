package edu.sjtu.se.dclab.agent;

import java.util.Map;

import edu.sjtu.se.dclab.entity.City;

public interface CityLoader {
	public Map<String, City> load();
}
