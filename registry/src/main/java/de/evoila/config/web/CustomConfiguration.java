package de.evoila.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Johannes Hiemer.
 * 
 */
@Configuration
@EnableAsync
public class CustomConfiguration {

	Logger log = LoggerFactory.getLogger(CustomConfiguration.class);

}
