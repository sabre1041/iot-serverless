package com.redhat.iot.model;

public class EventPayload {
	
	private final String topic;
	private final String data;
	
	public EventPayload(String topic, String data) {
		this.topic = topic;
		this.data = data;
	}

	public String getTopic() {
		return topic;
	}

	public String getData() {
		return data;
	}

}
