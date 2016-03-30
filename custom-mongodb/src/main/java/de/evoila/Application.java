/**
 * 
 */
package de.evoila;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import de.evoila.cf.cpi.custom.props.DomainBasedCustomPropertyHandler;
import de.evoila.cf.cpi.custom.props.MongoDBCustomPropertyHandler;
import de.evoila.cf.cpi.openstack.custom.CustomIpAccessor;
import de.evoila.cf.cpi.openstack.custom.MongoIpAccessor;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@SpringBootApplication
public class Application {

	@Value("${mongodb.security.key.length}")
	private int keyLength;

	@Bean
	public CustomIpAccessor mongoIpAccessor() {
		return new MongoIpAccessor();
	}

	@Bean(name = "customProperties")
	public Map<String, String> customProperties() {
		Map<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("database_name", "admin");

		return customProperties;
	}

	@Bean
	public DomainBasedCustomPropertyHandler domainPropertyHandler() {
		return new MongoDBCustomPropertyHandler(keyLength);
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

		Assert.notNull(ctx);
	}

}