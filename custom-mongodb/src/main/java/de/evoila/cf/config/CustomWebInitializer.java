package de.evoila.cf.config;

import de.evoila.cf.config.security.CustomSecurityConfiguration;
import de.evoila.cf.config.web.CustomMvcConfiguration;

/**
 * 
 * @author Johannes Hiemer.
 * 
 */
public class CustomWebInitializer extends BasedCustomWebInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { CustomSecurityConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { CustomMvcConfiguration.class };
	}
}