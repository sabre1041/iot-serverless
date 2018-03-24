package com.redhat.iot.model;

public class Asset {
	
	private String name;
	private String location;
	private String topic;
	private String latitude;
	private String longitude;
	private long frequency;
	private int iterations;
	private Double iterationChangeLatitude;
	private Double iterationChangeLongitude;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public long getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getIterations() {
		return iterations;
	}
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	public Double getIterationChangeLatitude() {
		return iterationChangeLatitude;
	}
	public void setIterationChangeLatitude(Double iterationChangeLatitude) {
		this.iterationChangeLatitude = iterationChangeLatitude;
	}
	public Double getIterationChangeLongitude() {
		return iterationChangeLongitude;
	}
	public void setIterationChangeLongitude(Double iterationChangeLongitude) {
		this.iterationChangeLongitude = iterationChangeLongitude;
	}
	

}
