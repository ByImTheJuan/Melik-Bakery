package com.hyd.pipes_bakery_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
public class PipesBakeryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PipesBakeryBackendApplication.class, args);
	}

}
