package com.redhat.iot.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.redhat.iot.model.Asset;

@ConfigurationProperties
@Configuration
public class AssetConfiguration {
	
	private List<Asset> assets;

	public List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

}
