/**
 * 
 */
package de.evoila.cf.cpi.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;

/**
 * @author Johannes Hiemer.
 *
 */
@Configuration
@ComponentScan(basePackages = { "de.evoila.cf.cpi", "de.evoila.cf.broker.service" })
public class IntegrationTestConfiguration {
	
	@Bean
	public PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("application.properties") };
		propertyPlaceholderConfigurer.setLocations(resources);
		propertyPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
		return propertyPlaceholderConfigurer;
	}
	
	@Bean
	public Catalog catalog() {
		Catalog catalog = new Catalog(Arrays.asList(serviceDefinition()));

		return catalog;
	}
	
	@Bean
	public ServiceDefinition serviceDefinition() {
		Plan dockerPlan = new Plan("docker", "25 MB PostgreSQL DB Docker Instance",
				"The most basic PostgreSQL plan currently available. Providing"
						+ "25 MB of capcity in a PostgreSQL DB.", Platform.DOCKER, 25, null, 4);
		Plan openstackPlan = new Plan("openstack", "500 MB PostgreSQL DB Openstack Instance",
				"The most basic PostgreSQL plan currently available. Providing"
						+ "500 MB of capcity in a PostgreSQL DB.", Platform.OPENSTACK, 500, "3", 10);

		ServiceDefinition serviceDefinition = new ServiceDefinition("postgres", "postgres", "PostgreSQL Instances",
				true, Arrays.asList(dockerPlan, openstackPlan));

		return serviceDefinition;
	}

}
