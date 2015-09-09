/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.CreateServiceInstanceResponse;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Christian Brinker.
 *
 */
@Service
public class DeploymentServiceImpl implements DeploymentService {

	@Autowired
	private StorageService storageService;
	
	public void addPlatformService(Platform platform, PlatformService platformService) {
		if (storageService.getPlatformService(platform) == null)
			storageService.addPlatform(platform, platformService);
		else 
			throw new BeanCreationException("Cannot add multiple instances of platform service to AbstractDeploymentService");
	}
	
	@Autowired(required=false)
	private AsyncDeploymentService asyncDeploymentService;

	@Override
	public JobProgress getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
		String progress = asyncDeploymentService.getProgress(serviceInstanceId);
		if (progress == null || !storageService.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}
		return new JobProgress(progress, "");
	}

	@Override
	public CreateServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException {

		storageService.validateServiceId(serviceDefinitionId);

		if (storageService.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, storageService.getServiceDefinition().getId());
		}
		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, storageService.getServiceDefinition().getId(), planId,
				organizationGuid, spaceGuid, parameters==null?null:new ConcurrentHashMap<String, String>(parameters));

		Plan plan = storageService.getPlan(planId);

		PlatformService platformService = storageService.getPlatformService(plan.getPlatform());

		if (platformService.isSyncPossibleOnCreate(plan) && platformService.isSyncPossibleOnCreate(plan)) {
			return syncCreateInstance(serviceInstance, plan, platformService);
		} else {
			ServiceInstance promise = platformService.getCreateInstancePromise(serviceInstance, plan);
			CreateServiceInstanceResponse creationResult = new CreateServiceInstanceResponse(promise, true);
			storageService.addServiceInstance(promise.getId(), serviceInstance);

			asyncDeploymentService.asyncCreateInstance(serviceInstance, plan, platformService);

			return creationResult;
		}
	}

	CreateServiceInstanceResponse syncCreateInstance(ServiceInstance serviceInstance, Plan plan,
			PlatformService platformService) throws ServiceBrokerException {
		ServiceInstance createdServiceInstance = platformService.createInstance(serviceInstance, plan);

		createdServiceInstance = platformService.postProvisioning(createdServiceInstance, plan);
		if (createdServiceInstance.getInternalId() != null)
			storageService.addServiceInstance(createdServiceInstance.getId(), serviceInstance);
		else {
			throw new ServiceBrokerException(
					"Internal error. Service instance was not created. ID was: " + serviceInstance.getId());
		}
		return new CreateServiceInstanceResponse(createdServiceInstance, false);
	}

	/**
	 * 
	 * @param instanceId
	 * @return
	 */
	protected String getInternalId(String instanceId) {
		return storageService.getServiceInstance(instanceId).getInternalId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#deleteServiceInstance(
	 * java.lang.String)
	 */
	@Override
	public void deleteServiceInstance(String instanceId)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		ServiceInstance serviceInstance = storageService.getServiceInstance(instanceId);

		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = storageService.getPlan(serviceInstance.getPlanId());

		PlatformService platformService = storageService.getPlatformService(plan.getPlatform());

		if (platformService.isSyncPossibleOnDelete(serviceInstance) && platformService.isSyncPossibleOnDelete(serviceInstance)) {
			syncDeleteInstance(serviceInstance, platformService);
		} else {
			asyncDeploymentService.asyncDeleteInstance(instanceId, serviceInstance, platformService);
		}

	}

	void syncDeleteInstance(ServiceInstance serviceInstance, PlatformService platformService)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		platformService.preDeprovisionServiceInstance(serviceInstance);

		platformService.deleteServiceInstance(serviceInstance);
	}
}
