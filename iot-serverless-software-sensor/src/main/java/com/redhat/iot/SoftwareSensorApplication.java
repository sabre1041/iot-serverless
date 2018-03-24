package com.redhat.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SoftwareSensorApplication {

	
    public static void main(String[] args) {
    	SpringApplication.run(SoftwareSensorApplication.class, args);
    }

}
