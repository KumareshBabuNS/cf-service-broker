package de.evoila.cf.config.web;

import java.util.Arrays;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

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
public class CustomMvcConfiguration extends WebMvcConfigurerAdapter {

	@Bean
	public Catalog catalog() {
		Catalog catalog = new Catalog(Arrays.asList(serviceDefinition()));
		
		return catalog;
	}
	
	@Bean
	public ServiceDefinition serviceDefinition() {
		Plan plan = new Plan("MongoDB Basic Plan", "500 MB MongoDB Basic Instance", "The most basic MongoDB plan currently available. Providing" +
				"500 MB of capcity in MongoDB.");
		
		ServiceDefinition serviceDefinition = new ServiceDefinition("mongoDB", 
				"MongoDB", "MongoDB Instances", true, Arrays.asList(plan));
		
		return serviceDefinition;
	}
		
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
    }
	
	@Bean
	public PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("application-mvc.properties") };
		propertyPlaceholderConfigurer.setLocations(resources);
		propertyPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
		return propertyPlaceholderConfigurer;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations(
				"/resources/");
	}

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("locale");
		registry.addInterceptor(localeChangeInterceptor);
	}
	
}
