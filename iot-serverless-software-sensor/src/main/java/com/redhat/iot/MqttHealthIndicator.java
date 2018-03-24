package com.redhat.iot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MqttHealthIndicator implements HealthIndicator {

	@Autowired
	private MqttClient mqttClient;
	
	@Override
	public Health health() {
		
		try {
			if(mqttClient.isConnected()){
				return Health.up().build();
			}
			else {
				return Health.down().build();
			}
		}
		catch(Exception e) {
			return Health.down().withDetail("Error", e.getMessage()).build();
		}
		
	}

}
