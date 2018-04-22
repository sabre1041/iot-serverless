package com.redhat.iot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.iot.model.Asset;

import java.math.BigDecimal;

public class AssetRunner implements Runnable {

	private volatile MqttClient mqttClient;
	private final Asset asset;
	private final AssetCallback assetCallback;
	private volatile int completedIterations = 0;
	private BigDecimal currentLatitude;
	private BigDecimal currentLongitude;

	private static final Logger LOGGER = LoggerFactory.getLogger(AssetRunner.class);

	public AssetRunner(final MqttClient mqttClient, final Asset asset, final AssetCallback assetCallback) {
		this.mqttClient = mqttClient;
		this.asset = asset;
		this.assetCallback = assetCallback;
	}

	@Override
	public void run() {

		if(currentLatitude == null) {
			currentLatitude = new BigDecimal(asset.getLatitude());
		}
		if(currentLongitude == null) {
			currentLongitude = new BigDecimal(asset.getLongitude());
		}

		LOGGER.info("Running Scheduled Task for Asset: {} - Iteration: {} - Latitude: {} - Longitude: {}", asset.getName(), completedIterations+1, currentLatitude, currentLongitude);

		if(mqttClient != null && mqttClient.isConnected()) {

			String messageContent = String.format("%s %s", currentLatitude, currentLongitude);


			try {
				MqttMessage message = new MqttMessage(messageContent.getBytes());
				message.setQos(2);
				mqttClient.publish(asset.getTopic(), message);
			}
			catch(MqttException e) {
				LOGGER.error(e.getMessage(), e);
			}

			currentLatitude = currentLatitude.add(asset.getIterationChangeLatitude());
			currentLongitude = currentLongitude.add(asset.getIterationChangeLongitude());
			completedIterations++;

			assetCallback.assetTaskComplete(this);
		}
		else {
			LOGGER.warn("MQTT Client is Not Connected. Skipping...");
		}

	}

	public Asset getAsset() {
		return asset;
	}

	public int getCompletedIterations() {
		return completedIterations;
	}


}
