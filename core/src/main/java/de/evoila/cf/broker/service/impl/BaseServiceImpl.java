/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Johannes Hiemer.
 *
 */
public class BaseServiceImpl {
	
	protected static final String SUCCESS = "success";

	protected static final String FAILED = "failed";

	protected static final String IN_PROGRESS = "in progress";
	
	protected ServiceDefinition serviceDefinition;
	
	protected Map<String, ServiceInstance> serviceInstances = new ConcurrentHashMap<String, ServiceInstance>();
	
	protected Map<String, String> internalBindingIdMapping = new ConcurrentHashMap<String, String>();
	
	protected Map<Platform, PlatformService> platformServices = new ConcurrentHashMap<Platform, PlatformService>();

	protected PlatformService getPlatformService(Plan plan) throws ServiceBrokerException {
		PlatformService platformService = platformServices.get(plan.getPlatform());
		if (platformService == null) {
			throw new ServiceBrokerException("No valid platform for plan with id: " + plan.getId());
		}
		return platformService;
	}

	protected Plan getPlan(ServiceDefinition service, String planId) throws ServiceBrokerException {
		for (Plan currentPlan : service.getPlans()) {
			if (currentPlan.getId().equals(planId)) {
				return currentPlan;
			}
		}
		throw new ServiceBrokerException("Missing plan for id: " + planId);
	}
	
	protected void validateServiceId(String serviceDefinitionId) throws ServiceDefinitionDoesNotExistException {
		if (!serviceDefinitionId.equals(serviceDefinition.getId())) {
			throw new ServiceDefinitionDoesNotExistException(serviceDefinitionId);
		}
	}
	
	protected ServiceInstance getServiceInstance(String id) {
		return this.serviceInstances.get(id);
	}

}
