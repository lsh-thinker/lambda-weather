package edu.sjtu.se.dclab.entity;

import java.io.Serializable;

public class City implements Serializable{

	private static final long serialVersionUID = -6901108143116055254L;
	
	
	private String cityId;
	private String cityName;
	private String firstLevelName;
	private String secondLevelName;
	
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getFirstLevelName() {
		return firstLevelName;
	}
	public void setFirstLevelName(String firstLevelName) {
		this.firstLevelName = firstLevelName;
	}
	public String getSecondLevelName() {
		return secondLevelName;
	}
	public void setSecondLevelName(String secondLevelName) {
		this.secondLevelName = secondLevelName;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityId == null) ? 0 : cityId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		if (cityId == null) {
			if (other.cityId != null)
				return false;
		} else if (!cityId.equals(other.cityId))
			return false;
		return true;
	}
	
	
	
}
