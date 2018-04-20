package com.redhat.iot.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
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
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
	public List<Asset> getAllAssets() {
		return assetRepository.findAll();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/latest", produces = "application/json")
	public List<Asset> getLatest() {
		
		SortOperation sort = sort(new Sort(Direction.ASC, "date"));
		GroupOperation group = group("topic")
					.last("date").as("date")
					.last("_id").as("id")
					.last("name").as("name")
					.last("geofence_radius").as("geofence_radius")
					.last("location").as("location")
					.last("topic").as("topic")
					.last("latitude").as("latitude")
					.last("longitude").as("longitude")
					.last("center_longitude").as("center_longitude")
					.last("center_latitude").as("center_latitude")
					.last("picture").as("picture")
					.last("alert").as("alert");
		
		ProjectionOperation project = project()
				.andExpression("id").as("_id")
				.andExpression("date").as("date")
				.andExpression("name").as("name")
				.andExpression("geofence_radius").as("geofence_radius")
				.andExpression("location").as("location")
				.andExpression("topic").as("topic")
				.andExpression("latitude").as("latitude")
				.andExpression("longitude").as("longitude")
				.andExpression("center_longitude").as("center_longitude")
				.andExpression("center_latitude").as("center_latitude")
				.andExpression("picture").as("picture")
				.andExpression("alert").as("alert");

		Aggregation aggregation = newAggregation(sort, group, project);
		
		AggregationResults<Asset> result = mongoTemplate.aggregate(aggregation, "results", Asset.class);

		return result.getMappedResults();

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
