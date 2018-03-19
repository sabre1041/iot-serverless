package com.redhat.iot.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.redhat.iot.model.TriggerData;
import com.redhat.iot.repository.TriggerRepository;

@Service
public class TriggerDataService {
	
	private TriggerRepository triggerRepository;
	
	private final Logger LOGGER = LoggerFactory.getLogger(TriggerDataService.class);
	
	public TriggerDataService(TriggerRepository triggerRepository) {
		this.triggerRepository = triggerRepository;
	}
	
	public boolean saveOrUpdate(TriggerData triggerData){
		
		TriggerData resolvedTriggerData = triggerRepository.findByTriggerName(triggerData.getTriggerName());
		
		if(resolvedTriggerData != null) {
			LOGGER.info("Existing Record Found");
			triggerData.setId(resolvedTriggerData.getId());			
		}
		
		LOGGER.info("Saving Trigger");
		triggerRepository.save(triggerData);
		
		return true;
	}

	public List<TriggerData> findAll() {
		return triggerRepository.findAll();
	}
	
	public boolean delete(TriggerData triggerData) {
		triggerRepository.delete(triggerData);
		return true;
	}
	
	public List<TriggerData> getTriggersByTopic(String topic) {
		return triggerRepository.findByTopic(topic);
	}
	
	public TriggerData getTriggerByName(String triggerName) {
		return triggerRepository.findByTriggerName(triggerName);
	}
	

}
