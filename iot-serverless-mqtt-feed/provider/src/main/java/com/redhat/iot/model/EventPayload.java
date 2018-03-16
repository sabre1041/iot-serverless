package com.redhat.iot.model;

public class EventPayload {
	
	private final String data;
	
	public EventPayload(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

}
