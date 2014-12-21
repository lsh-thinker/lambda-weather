package edu.sjtu.se.dclab.weather;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import kafka.message.Message;
import kafka.serializer.Decoder;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;
import edu.sjtu.se.dclab.entity.WeatherInfo;

public class WeatherSerializer implements Encoder<WeatherInfo>, Decoder<WeatherInfo>{
	
	
	public WeatherSerializer(VerifiableProperties props){
		
	}
	
	public Message toMessage(WeatherInfo info){
		return new Message(toBytes(info));
	}

	@Override
	public byte[] toBytes(WeatherInfo info) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(info);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}
	
	@Override
	public WeatherInfo fromBytes(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		WeatherInfo info = null;
		try {
			ois = new ObjectInputStream(bais);
			info = (WeatherInfo) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return info;
	}
	
}
