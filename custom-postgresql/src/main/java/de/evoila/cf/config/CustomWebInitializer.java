/**
 * 
 */
package de.evoila.cf.config;

import de.evoila.cf.config.security.CustomSecurityConfiguration;
import de.evoila.cf.config.web.CustomMvcConfiguration;

/**
 * @author Christian
 *
 */
public class CustomWebInitializer extends BasedCustomWebInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.
	 * AbstractAnnotationConfigDispatcherServletInitializer#getRootConfigClasses
	 * ()
	 */
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { CustomSecurityConfiguration.class };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.support.
	 * AbstractAnnotationConfigDispatcherServletInitializer#
	 * getServletConfigClasses()
	 */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { CustomMvcConfiguration.class };
	}
}
