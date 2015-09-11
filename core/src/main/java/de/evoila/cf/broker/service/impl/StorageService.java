/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class StorageService {

	@Autowired
	protected ServiceDefinition serviceDefinition;

	public ServiceDefinition getServiceDefinition() {
		return serviceDefinition;
	}

	public Map<String, ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public ServiceInstance getServiceInstance(String instanceId) {
		return this.serviceInstances.get(instanceId);
	}

	public Map<String, String> getInternalBindingIdMapping() {
		return internalBindingIdMapping;
	}

	public String getInternalBindingId(String bindingId) {
		return this.internalBindingIdMapping.get(bindingId);
	}

	public void addInternalBinding(String bindingId, String id) {
		this.internalBindingIdMapping.put(bindingId, id);
	}

	public boolean containsInternalBindingId(String bindingId) {
		return this.internalBindingIdMapping.containsKey(bindingId);
	}

	public Map<Platform, PlatformService> getPlatformServices() {
		return platformServices;
	}

	public void addPlatform(Platform platform, PlatformService platformService) {
		this.platformServices.put(platform, platformService);
	}

	protected PlatformService getPlatformService(Platform platform) {
		return platformServices.get(platform);
	}

	public boolean containsServiceInstanceId(String serviceInstanceId) {
		return this.serviceInstances.containsKey(serviceInstanceId);
	}

	private Map<String, ServiceInstance> serviceInstances = new ConcurrentHashMap<String, ServiceInstance>();

	private Map<String, String> internalBindingIdMapping = new ConcurrentHashMap<String, String>();

	private Map<Platform, PlatformService> platformServices = new ConcurrentHashMap<Platform, PlatformService>();

	protected Plan getPlan(String planId) throws ServiceBrokerException {
		for (Plan currentPlan : serviceDefinition.getPlans()) {
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

	public void addServiceInstance(String id, ServiceInstance serviceInstance) {
		serviceInstances.put(id, serviceInstance);
	}

}
