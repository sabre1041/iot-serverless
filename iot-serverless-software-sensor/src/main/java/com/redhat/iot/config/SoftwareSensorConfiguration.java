package com.redhat.iot.config;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.redhat.iot.AssetRunner;
import com.redhat.iot.SoftwareSensorTaskScheduler;

@Configuration
@EnableScheduling
public class SoftwareSensorConfiguration implements SchedulingConfigurer {

	@Autowired
	private AssetConfiguration assetConfiguration;
	
	@Autowired
	private MqttClient mqttClient;
	
	private int poolSizeMultiplier = 2;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
		TaskScheduler scheduler = scheduler();
		
		taskRegistrar.setTaskScheduler(scheduler);
		
		assetConfiguration.getAssets().forEach(asset -> {
			
			AssetRunner assetRunner = new AssetRunner(mqttClient, asset, (SoftwareSensorTaskScheduler)scheduler);
			
			long frequency = asset.getFrequency();
			
			if(frequency <= 0) {
				taskRegistrar.getScheduler().schedule(assetRunner, new Date());
			}
			else {
				taskRegistrar.addFixedRateTask(assetRunner, frequency);
			}
			
		});
		
	}

	
	@Bean
	public TaskScheduler scheduler() {
		SoftwareSensorTaskScheduler scheduler = new SoftwareSensorTaskScheduler();
		scheduler.setPoolSize(assetConfiguration.getAssets().size() * poolSizeMultiplier);
		
		return scheduler;
	}

}
