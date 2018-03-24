package com.redhat.iot.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.redhat.iot.config.OpenWhiskConfiguration;
import com.redhat.iot.model.EventPayload;
import com.redhat.iot.model.TriggerData;
import com.redhat.iot.util.Utils;

@Service
public class OpenWhiskService {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	OpenWhiskConfiguration openWhiskConfiguration;
	
	@Autowired
	TriggerDataService triggerDataService;
		
	private final Logger LOGGER = LoggerFactory.getLogger(OpenWhiskService.class);
	
	public void invokeTriggers(String topic, EventPayload eventPayload) {

		List<TriggerData> triggerDatas = triggerDataService.getTriggersByTopicDestination(topic);
		System.out.println("Trigger Datas Size: "+triggerDatas.size());
		
		triggerDatas.parallelStream().forEach(triggerData -> {
			
			String url = String.format("%s%s/%s/triggers/%s", openWhiskConfiguration.getApiHost(), openWhiskConfiguration.getNamespaceEndpoint(), triggerData.getTriggerNamespace(), triggerData.getShortTriggerName());
			LOGGER.info("About to Invoke: {}", url);
			
		    try {
		    			    	
		        HttpHeaders httpHeaders = new HttpHeaders();
		        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		        httpHeaders.set("Authorization", "Basic " + Utils.base64Encoded(triggerData.getAuthKey()));

		        HttpEntity<EventPayload> requestEntity = new HttpEntity<>(eventPayload, httpHeaders);

		        ResponseEntity<String> response = restTemplate.exchange(url,
		          HttpMethod.POST, requestEntity, String.class);

		        LOGGER.info("Status: {}  Response body:{}", response.getStatusCode().value(), response.getBody());


		      } catch (Exception e1) {
		        LOGGER.error("Error with trigger " + triggerData.getShortTriggerName(), e1);
		      }

			
			

		});
		
	}

}
