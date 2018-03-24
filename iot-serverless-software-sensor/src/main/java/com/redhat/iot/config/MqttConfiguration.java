package com.redhat.iot.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfiguration {
	
	@Value("${mqtt.username}")
	private String mqttUsername;
	
	@Value("${mqtt.password}")
	private String mqttPassword;
	
	@Value("${mqtt.url}")
	private String mqttUrl;

	@Value("${mqtt.clientId}")
	private String mqttClientId;
	
	@Bean(destroyMethod="disconnect")
	public MqttClient mqttClient() throws MqttException {

		MqttClient mqttClient = new MqttClient(mqttUrl, mqttClientId);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setUserName(mqttUsername);
		connOpts.setPassword(mqttPassword.toCharArray());
		connOpts.setCleanSession(true);
		connOpts.setConnectionTimeout(5000);
		connOpts.setAutomaticReconnect(true);
		mqttClient.connect(connOpts);
		
		return mqttClient;
	}
	
	
	


}
