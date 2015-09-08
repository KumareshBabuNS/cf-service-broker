/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.scheduling.annotation.Async;
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
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Christian Brinker.
 *
 */
@Service
public class DeploymentServiceImpl extends BaseServiceImpl implements DeploymentService {

	private Map<String, String> jobProgress = new ConcurrentHashMap<String, String>();

	public void addPlatformService(Platform platform, PlatformService platformService) {
		if (platformServices.get(platform) == null)
			platformServices.put(platform, platformService);
		else 
			throw new BeanCreationException("Cannot add multiple instances of platform service to AbstractDeploymentService");
	}

	@Override
	public JobProgress getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
		String progress = jobProgress.get(serviceInstanceId);
		if (progress == null || serviceInstances.containsKey(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}
		return new JobProgress(progress, "");
	}

	@Override
	public CreateServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException {

		validateServiceId(serviceDefinitionId);

		if (serviceInstances.containsKey(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, serviceDefinition.getId());
		}
		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, serviceDefinition.getId(), planId,
				organizationGuid, spaceGuid, new ConcurrentHashMap<String, String>(parameters));

		Plan plan = getPlan(serviceDefinition, planId);

		PlatformService platformService = getPlatformService(plan);

		if (platformService.isSyncPossibleOnCreate(plan) && platformService.isSyncPossibleOnCreate(plan)) {
			return syncCreateInstance(serviceInstance, plan, platformService);
		} else {
			ServiceInstance promise = platformService.getCreateInstancePromise(serviceInstance, plan);
			CreateServiceInstanceResponse creationResult = new CreateServiceInstanceResponse(promise, true);
			serviceInstances.put(promise.getId(), serviceInstance);

			asyncCreateInstance(serviceInstance, plan, platformService);

			return creationResult;
		}
	}

	@Async
	private void asyncCreateInstance(ServiceInstance serviceInstance, Plan plan, PlatformService platformService) {
		jobProgress.put(serviceInstance.getId(), IN_PROGRESS);

		try {
			syncCreateInstance(serviceInstance, plan, platformService);
		} catch (ServiceBrokerException e) {
			jobProgress.put(serviceInstance.getId(), FAILED);
			e.printStackTrace();
		}

		jobProgress.put(serviceInstance.getId(), SUCCESS);
	}

	private CreateServiceInstanceResponse syncCreateInstance(ServiceInstance serviceInstance, Plan plan,
			PlatformService platformService) throws ServiceBrokerException {
		ServiceInstance createdServiceInstance = platformService.createInstance(serviceInstance, plan);

		createdServiceInstance = platformService.postProvisioning(createdServiceInstance, plan);
		if (createdServiceInstance.getInternalId() != null)
			serviceInstances.put(createdServiceInstance.getId(), serviceInstance);
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
		return this.serviceInstances.get(instanceId).getInternalId();
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
		ServiceInstance serviceInstance = getServiceInstance(instanceId);

		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = getPlan(serviceDefinition, serviceInstance.getPlanId());

		PlatformService platformService = getPlatformService(plan);

		if (platformService.isSyncPossibleOnDelete(serviceInstance) && platformService.isSyncPossibleOnDelete(serviceInstance)) {
			syncDeleteInstance(serviceInstance, platformService);
		} else {
			asyncDeleteInstance(instanceId, serviceInstance, platformService);
		}

	}

	@Async
	private void asyncDeleteInstance(String instanceId, ServiceInstance serviceInstance,
			PlatformService platformService) throws ServiceInstanceDoesNotExistException {
		jobProgress.put(instanceId, IN_PROGRESS);

		try {
			syncDeleteInstance(serviceInstance, platformService);
		} catch (ServiceBrokerException e) {
			jobProgress.put(instanceId, FAILED);
			e.printStackTrace();
		}

		jobProgress.put(instanceId, SUCCESS);
	}

	private void syncDeleteInstance(ServiceInstance serviceInstance, PlatformService platformService)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		platformService.preDeprovisionServiceInstance(serviceInstance);

		platformService.deleteServiceInstance(serviceInstance);
	}

}
