package com.redhat.iot.rest;

import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.iot.model.Asset;
import com.redhat.iot.repository.AssetRepository;

@RequestMapping("/assets")
@RestController
public class AssetController {
	
	@Autowired
	private AssetRepository assetRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
	public List<Asset> getAllAssets() {
		return assetRepository.findAll();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/latest", produces = "application/json")
	public List<Asset> getLatest() {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
	public Asset findById(@PathVariable("id") String id) {
		return assetRepository.findById(id);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/topic/{inputTopic}/**", produces = "application/json")
	public List<Asset> findByTopic(@PathVariable("inputTopic") String inputTopic, HttpServletRequest request) {
		
		String triggerName = new AntPathMatcher()
	            .extractPathWithinPattern( "/topic/{inputTopic}**", request.getRequestURI()).replaceAll("topic", "");

		return assetRepository.findByTopic(triggerName);
	}
	

}
