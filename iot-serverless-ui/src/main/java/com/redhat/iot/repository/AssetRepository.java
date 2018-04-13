package com.redhat.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.redhat.iot.model.Asset;

public interface AssetRepository extends MongoRepository<Asset, Integer> {
	
	List<Asset> findByTopic(String topic);
	Asset findById(String id);
}
