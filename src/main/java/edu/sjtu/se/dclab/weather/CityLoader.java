package edu.sjtu.se.dclab.weather;

import java.util.Map;

import edu.sjtu.se.dclab.entity.City;

public interface CityLoader {
	public Map<String, City> load();
}
