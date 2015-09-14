package de.evoila.config.web;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.VolumeUnit;

/**
 * @author Johannes Hiemer.
 * 
 */
@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = { "de.evoila.cf.broker", "de.evoila.cf.cpi" })
public class CustomMvcConfiguration extends WebMvcConfigurerAdapter implements AsyncConfigurer {

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);
        executor.setMaxPoolSize(42);
        executor.setQueueCapacity(11);
        executor.setThreadNamePrefix("MyExecutor-");
        executor.initialize();
        return executor;
	}

	@Bean
	public PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("application-mvc.properties"),
				new ClassPathResource("openstack.properties"), new ClassPathResource("container.properites") };
		propertyPlaceholderConfigurer.setLocations(resources);
		propertyPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
		return propertyPlaceholderConfigurer;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
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

	@Bean
	public Catalog catalog() {
		Catalog catalog = new Catalog(Arrays.asList(serviceDefinition()));

		return catalog;
	}

	@Bean
	public ServiceDefinition serviceDefinition() {
		Plan dockerPlan = new Plan("docker", "PostgreSQL-Docker-25MB",
				"The most basic PostgreSQL plan currently available. Providing"
						+ "25 MB of capcity in a PostgreSQL DB.", Platform.DOCKER, 25, VolumeUnit.M, null, 4);
		Plan openstackPlan = new Plan("openstack", "PostgreSQL-VM-500MB",
				"The most basic PostgreSQL plan currently available. Providing"
						+ "500 MB of capcity in a PostgreSQL DB.", Platform.OPENSTACK, 1, VolumeUnit.G, "3", 10);

		ServiceDefinition serviceDefinition = new ServiceDefinition("postgres", "postgres", "PostgreSQL Instances",
				true, Arrays.asList(dockerPlan, openstackPlan));

		return serviceDefinition;
	}
	
}
