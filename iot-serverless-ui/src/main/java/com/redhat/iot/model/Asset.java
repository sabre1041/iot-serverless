package com.redhat.iot.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(collection="results")
public class Asset {
	
	@Id
	private String id;
	
	@Field(value="name")
	private String name;
	
	@Field(value="location")
	private String location;
	
	@Field(value="latitude")
	private String latitude;
	
	@Field(value="longitude")
	private String longitude;
	
	@Field(value="geofence_radius")
	private String geofenceRadius;
	
	@Field(value="alert")
	private int alert;
	
	@Field(value="topic")
	private String topic;
	
	@Field(value="center_latitude")
	private String centerLatitude;
	
	@Field(value="center_longitude")
	private String centerLongitude;
	
	@DateTimeFormat(iso=ISO.DATE_TIME)
	@Field(value="date")
	private Date date;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getGeofenceRadius() {
		return geofenceRadius;
	}
	public void setGeofenceRadius(String geofenceRadius) {
		this.geofenceRadius = geofenceRadius;
	}
	public int isAlert() {
		return alert;
	}
	public void setAlert(int alert) {
		this.alert = alert;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getCenterLatitude() {
		return centerLatitude;
	}
	public void setCenterLatitude(String centerLatitude) {
		this.centerLatitude = centerLatitude;
	}
	public String getCenterLongitude() {
		return centerLongitude;
	}
	public void setCenterLongitude(String centerLongitude) {
		this.centerLongitude = centerLongitude;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
