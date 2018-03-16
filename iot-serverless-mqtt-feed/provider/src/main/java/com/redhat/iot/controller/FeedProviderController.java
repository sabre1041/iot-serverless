/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.iot.controller;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerMapping;

import com.redhat.iot.exception.UnprocessableEntityException;
import com.redhat.iot.model.EventPayload;
import com.redhat.iot.model.TriggerData;
import com.redhat.iot.repository.TriggerRepository;
import com.redhat.iot.service.OpenWhiskService;
import com.redhat.iot.service.TriggerDataService;

@Controller
@RequestMapping(value = "/api/feed")
public class FeedProviderController {

	private final Logger LOGGER = LoggerFactory.getLogger(FeedProviderController.class);

	private final OpenWhiskService openWhiskService;
	private final TriggerDataService triggerDataService;
	private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;
	private final JmsListenerContainerFactory jmsFeedContainerFactory;

	@Autowired
	public FeedProviderController(TriggerRepository repository,
			OpenWhiskService openWhiskService,
			TriggerDataService triggerDataService,
			JmsListenerEndpointRegistry jmsListenerEndpointRegistry,
			JmsListenerContainerFactory jmsFeedContainerFactory) {
		this.openWhiskService = openWhiskService;
		this.triggerDataService = triggerDataService;
		this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
		this.jmsFeedContainerFactory = jmsFeedContainerFactory;
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void addFeedToTrigger(@RequestBody(required = false) TriggerData triggerData) {

		LOGGER.info("Trigger Name: {}", triggerData.getTriggerName());
		
		String topicName = "iot-serverless";
		
		if(triggerData.getTopic() != null) {
			topicName = triggerData.getTopic();
		}
		else {
			triggerData.setTopic(topicName);
		}
		
		String[] triggerNameComponents = triggerData.getTriggerName().split("/");
		
		if(triggerNameComponents.length != 3) {
			throw new UnprocessableEntityException("Invalid Trigger Name");
		}
		
		triggerData.setTriggerNamespace(triggerNameComponents[1]);
		triggerData.setShortTriggerName(triggerNameComponents[2]);

		addListenerEndpointToRegistry(triggerData.getTriggerName(), triggerData.getTopic());
		
		// Save to Database
		triggerDataService.saveOrUpdate(triggerData);
		
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{inputTriggerName}/**")
	public void removeFeedToTrigger(@PathVariable("inputTriggerName") String inputTriggerName, HttpServletRequest request) {
		
		String triggerName = new AntPathMatcher()
	            .extractPathWithinPattern( "/{inputTriggerName}/**", request.getRequestURI()).replaceAll("feed", "");

		LOGGER.info("Removing Trigger With Name: {}", triggerName);
		
		TriggerData triggerData = triggerDataService.getTriggerByName(triggerName);
		
		if(triggerData != null) {
			removeListenerEndpointFromRegistry(triggerData.getTriggerName());
			
			triggerDataService.delete(triggerData);
		}
		
	}
	
	private SimpleJmsListenerEndpoint addListenerEndpointToRegistry(String id, String destinationName) {

		SimpleJmsListenerEndpoint jmsListenerEndpoint = new SimpleJmsListenerEndpoint();
	    jmsListenerEndpoint.setDestination(destinationName);
	    jmsListenerEndpoint.setId(id);
	    jmsListenerEndpoint.setDestination(destinationName);
	    jmsListenerEndpoint.setMessageListener(message -> {
	    	
	    	
    	    String messageContent = null;
    	    String topicDestinationName = null;

	    	
	    	if(message instanceof ActiveMQBytesMessage){
	    		
	    		ActiveMQBytesMessage mqBytesMessage = (ActiveMQBytesMessage)message;
	    		messageContent = new String(mqBytesMessage.getContent().getData());
	    		
	 	    }
	    	else if(message instanceof ActiveMQTextMessage) {
	    		ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
	    		messageContent = new String(textMessage.getContent().getData());
	    	}
	    	
	    	try {
				Destination destination = message.getJMSDestination();
				if(destination instanceof ActiveMQTopic) {
					ActiveMQTopic activeMQTopic = (ActiveMQTopic) destination;
					topicDestinationName = activeMQTopic.getPhysicalName();
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	EventPayload eventPayload = new EventPayload(messageContent);
	    	
	    	openWhiskService.invokeTriggers(topicDestinationName, eventPayload);
	    	
	    });
	    
	    if (!jmsListenerEndpointRegistry.getListenerContainerIds().contains(id)) {
	        jmsListenerEndpointRegistry.registerListenerContainer(jmsListenerEndpoint,
	          jmsFeedContainerFactory, true);
	      } else {
	        MessageListenerContainer listenerContainer = jmsListenerEndpointRegistry.getListenerContainer(id);
	        if (listenerContainer != null && !listenerContainer.isRunning()) {
	          listenerContainer.start();
	        }
	      }

	      return jmsListenerEndpoint;
	}
	
	  private void removeListenerEndpointFromRegistry(String endpointId) {
		    MessageListenerContainer tobeDeleted = jmsListenerEndpointRegistry.getListenerContainer(endpointId);
		    if (tobeDeleted != null) {
		      tobeDeleted.stop(() -> {
		        LOGGER.info("Successfully stopped container {}", endpointId);
		      });
		    }
		  }


}
