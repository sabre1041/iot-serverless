package com.redhat.iot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openwhisk")
public class OpenWhiskConfiguration {
	
	private String apiHost;
	private String namespaceEndpoint;

	public String getApiHost() {
		return apiHost;
	}

	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}

	public String getNamespaceEndpoint() {
		return namespaceEndpoint;
	}

	public void setNamespaceEndpoint(String namespaceEndpoint) {
		this.namespaceEndpoint = namespaceEndpoint;
	}

}
