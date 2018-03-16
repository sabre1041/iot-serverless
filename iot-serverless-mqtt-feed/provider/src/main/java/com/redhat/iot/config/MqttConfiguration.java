package com.redhat.iot.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.web.client.RestTemplate;

import com.redhat.iot.service.OpenWhiskService;

@Configuration
@EnableConfigurationProperties(OpenWhiskConfiguration.class)
@EnableJms
public class MqttConfiguration implements JmsListenerConfigurer {

	@Autowired
	ConnectionFactory connectionFactory;

	@Bean
	public OpenWhiskService openWhiskService() {
		return new OpenWhiskService();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public JmsListenerContainerFactory jmsFeedContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setPubSubDomain(true);
		factory.setConnectionFactory(connectionFactory);
		return factory;
	}

	@Bean
	public JmsListenerEndpointRegistry jmsListenerEndpointRegistry() {
		return new JmsListenerEndpointRegistry();
	}

	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		registrar.setContainerFactory(jmsFeedContainerFactory());
		registrar.setEndpointRegistry(jmsListenerEndpointRegistry());
	}

}
