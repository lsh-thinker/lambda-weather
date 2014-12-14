package edu.sjtu.se.dclab.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WeatherInfo implements Serializable{
	private static final Logger LOG = LoggerFactory.getLogger(WeatherInfo.class);
	private static final long serialVersionUID = -4961198512767608590L;
	
	public static final String _WEATHER_INFO = "weatherinfo"; 
	public static final String _CITY = "city";
	public static final String _CITY_ID = "cityid";
	public static final String _TEMP = "temp";
	public static final String _WD = "WD";
	public static final String _WS = "WS";
	public static final String _SD = "SD";
	public static final String _WSE = "WSE";
	public static final String _TIME = "time";
	public static final String _IS_RADAR = "isRadar";
	public static final String _RADAR = "Radar";
	public static final String _NJD = "njd";
	public static final String _QY = "qy";
	
	public static final String _DATE_TIME = "datetime";
	
	
	//城市名称
	private String city;
	//城市编号
	private String cityId;
	//当前温度
	private String temp;
	//风向
	private String WD;
	//风速
	private String WS;
	//相对湿度
	private String SD;
	//风力
	private String WSE;
	//更新时间
	private String time;
	//是否有雷达图，1表示有
	private String isRadar;
	//雷达图地址（AZ9010为北京雷达）
	private String Radar;
	//能见度
	private String njd;
	//气压
	private String qy;
	
	//日期
	private DateTime dateTime;
	
	public WeatherInfo(JSONObject object){
		JSONObject jsonValues = (JSONObject)object.get(_WEATHER_INFO);
		city = (String)jsonValues.get(_CITY);
		cityId = (String)jsonValues.get(_CITY_ID);
		temp = (String)jsonValues.get(_TEMP);
		WD = (String)jsonValues.get(_WD);
		WS = (String)jsonValues.get(_WS);
		SD = (String)jsonValues.get(_SD);
		WSE = (String)jsonValues.get(_WSE);
		isRadar = (String)jsonValues.get(_IS_RADAR);
		Radar = (String)jsonValues.get(_RADAR);
		njd = (String)jsonValues.get(_NJD);
		qy = (String)jsonValues.get(_QY);
		time = (String)jsonValues.get(_TIME);
	}
	
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getWD() {
		return WD;
	}
	public void setWD(String wD) {
		WD = wD;
	}
	public String getWS() {
		return WS;
	}
	public void setWS(String wS) {
		WS = wS;
	}
	public String getSD() {
		return SD;
	}
	public void setSD(String sD) {
		SD = sD;
	}

	public String getWSE() {
		return WSE;
	}


	public void setWSE(String wSE) {
		WSE = wSE;
	}

	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getRadar() {
		return Radar;
	}
	public void setRadar(String radar) {
		Radar = radar;
	}
	
	
	
	public String getIsRadar() {
		return isRadar;
	}


	public void setIsRadar(String isRadar) {
		this.isRadar = isRadar;
	}


	public String getNjd() {
		return njd;
	}


	public void setNjd(String njd) {
		this.njd = njd;
	}


	public String getQy() {
		return qy;
	}


	public void setQy(String qy) {
		this.qy = qy;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cityId == null) ? 0 : cityId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		WeatherInfo other = (WeatherInfo) obj;
		if (cityId == null) {
			if (other.cityId != null)
				return false;
		} else if (!cityId.equals(other.cityId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
	
	@Override
	public String toString(){
//		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//		String json = "";
//		try {
//			json = ow.writeValueAsString(this);
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		JSONObject object = new JSONObject();
		object.put(_CITY, getCity());
		object.put(_CITY_ID, getCityId());
		object.put(_WD, getWD());
		object.put(_WS, getWS());
		object.put(_WSE, getWSE());
		object.put(_SD, getSD());
		object.put(_NJD, getNjd());
		object.put(_QY, getQy());
		object.put(_TIME, getTime());
		object.put(_IS_RADAR, getIsRadar());
		object.put(_RADAR, getRadar());
		object.put(_DATE_TIME, getDateTime().getMillis());
		return object.toJSONString();
	}
	
}
