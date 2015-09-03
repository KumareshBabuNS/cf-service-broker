package de.evoila.cf.config.web;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceDefinition;

/**
 * @author Johannes Hiemer, cloudscale.
 * 
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.pivotal.cf.broker")
public class CustomMvcConfiguration extends BaseMvcCustomConfiguration {

	@Bean
	public Catalog catalog() {
		Catalog catalog = new Catalog(Arrays.asList(serviceDefinition()));
		
		return catalog;
	}
	
	@Bean
	public ServiceDefinition serviceDefinition() {
		Plan plan = new Plan("PostgreSQL Basic Plan", "500 MB PostgreSQL DB Basic Instance", "The most basic PostgreSQL plan currently available. Providing" +
				"500 MB of capcity in a PostgreSQL DB.");
		
		ServiceDefinition serviceDefinition = new ServiceDefinition("postgres", 
				"postgres", "PostgreSQL Instances", true, Arrays.asList(plan));
		
		return serviceDefinition;
	}
	
}
