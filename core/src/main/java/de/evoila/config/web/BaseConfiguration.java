/**
 * 
 */
package de.evoila.config.web;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import de.evoila.cf.broker.model.Catalog;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.config.web.cors.CORSFilter;

/**
 * @author johanneshiemer
 *
 */
@Configuration
public class BaseConfiguration {
	
	@Bean
	public Catalog catalog() {
		Catalog catalog = new Catalog(Arrays.asList(serviceDefinition()));

		return catalog;
	}

	@Bean
	public ServiceDefinition serviceDefinition() {
		ClassPathResource classPathResource = new ClassPathResource("/plans/service-definition.yml");
		CustomClassLoaderConstructor constructor = new CustomClassLoaderConstructor(ServiceDefinition.class, ServiceDefinition.class.getClassLoader());

		Yaml yaml = new Yaml(constructor);

		ServiceDefinition serviceDefinition = null;
		try {
			serviceDefinition = yaml.loadAs(classPathResource.getInputStream(), ServiceDefinition.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		serviceDefinition.setRequires(Arrays.asList("syslog_drain"));

		return serviceDefinition;
	}
	
	/**
	@Bean
	public PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("persistence.properties"),
				new ClassPathResource("/cpi/openstack.properties"),
				new ClassPathResource("/cpi/container.properties") };
		propertyPlaceholderConfigurer.setLocations(resources);
		propertyPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
		return propertyPlaceholderConfigurer;
	}
	**/
	 
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CORSFilter());
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        return registration;
    }

}
