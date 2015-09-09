package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * 
 * @author Dennis Mueller, evoila GmbH, Sep 9, 2015
 *
 */

public interface AsyncDeploymentService {

	void asyncCreateInstance(ServiceInstance serviceInstance, Plan plan, PlatformService platformService);
	
	void asyncDeleteInstance(String instanceId, ServiceInstance serviceInstance,
			PlatformService platformService) throws ServiceInstanceDoesNotExistException;

	String getProgress(String serviceInstanceId);
}
