/**
 * 
 */
package de.evoila;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import de.evoila.cf.cpi.custom.props.DomainBasedCustomPropertyHandler;
import de.evoila.cf.cpi.custom.props.RabbitMQCustomPropertyHandler;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

	@Bean
	public DomainBasedCustomPropertyHandler domainPropertyHandler() {
		return new RabbitMQCustomPropertyHandler();
	}

	@Bean(name = "customProperties")
	public Map<String, String> customProperties() {
		Map<String, String> customProperties = new HashMap<String, String>();

		return customProperties;
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

		Assert.notNull(ctx);
	}

}