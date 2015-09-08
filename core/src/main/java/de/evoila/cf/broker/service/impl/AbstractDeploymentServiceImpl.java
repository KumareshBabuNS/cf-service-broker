/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.CreateServiceInstanceResponse;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.DeploymentService;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Christian
 *
 */
public abstract class AbstractDeploymentServiceImpl implements DeploymentService {

	private static final String SUCCESS = "success";

	private static final String FAILED = "failed";

	private static final String IN_PROGRESS = "in progress";

	protected Map<String, ServiceInstance> serviceInstances = new ConcurrentHashMap<String, ServiceInstance>();

	private Map<String, String> internalBindingIdMapping = new ConcurrentHashMap<String, String>();

	private Map<String, String> jobProgress = new ConcurrentHashMap<String, String>();

	@Autowired
	private Map<Platform, PlatformService> platformServices;

	@Autowired
	private ServiceDefinition serviceDefinition;

	@Override
	public JobProgress getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
		String progress = jobProgress.get(serviceInstanceId);
		if (progress == null || serviceInstances.containsKey(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}
		return new JobProgress(progress, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#createServiceInstance(
	 * de.evoila.cf.broker.model.ServiceDefinition, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
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

		if (this.isSyncPossibleOnCreate(plan) && platformService.isSyncPossibleOnCreate(plan)) {
			return syncCreateInstance(serviceInstance, plan, platformService);
		} else {
			ServiceInstance promiss = platformService.getCreateInstancePromiss(serviceInstance, plan);
			CreateServiceInstanceResponse creationResult = new CreateServiceInstanceResponse(promiss, true);
			serviceInstances.put(promiss.getId(), serviceInstance);

			asyncCreateInstance(serviceInstance, plan, platformService);

			return creationResult;
		}
	}

	private void validateServiceId(String serviceDefinitionId) throws ServiceDefinitionDoesNotExistException {
		if (!serviceDefinitionId.equals(serviceDefinition.getId())) {
			throw new ServiceDefinitionDoesNotExistException(serviceDefinitionId);
		}
	}

	protected abstract boolean isSyncPossibleOnCreate(Plan plan);

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

		createdServiceInstance = postProvisioning(createdServiceInstance, plan);
		if (createdServiceInstance.getInternalId() != null)
			serviceInstances.put(createdServiceInstance.getId(), serviceInstance);
		else {
			throw new ServiceBrokerException(
					"Internal error. Service instance was not created. ID was: " + serviceInstance.getId());
		}
		return new CreateServiceInstanceResponse(createdServiceInstance, false);
	}

	private PlatformService getPlatformService(Plan plan) throws ServiceBrokerException {
		PlatformService platformService = platformServices.get(plan.getPlatform());
		if (platformService == null) {
			throw new ServiceBrokerException("No valid platform for plan with id: " + plan.getId());
		}
		return platformService;
	}

	private Plan getPlan(ServiceDefinition service, String planId) throws ServiceBrokerException {
		for (Plan currentPlan : service.getPlans()) {
			if (currentPlan.getId().equals(planId)) {
				return currentPlan;
			}
		}
		throw new ServiceBrokerException("Missing plan for id: " + planId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#getServiceInstance(
	 * java.lang.String)
	 */
	private ServiceInstance getServiceInstance(String id) {
		return this.serviceInstances.get(id);
	}

	protected abstract ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException;

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

		if (this.isSyncPossibleOnDelete(serviceInstance) && platformService.isSyncPossibleOnDelete(serviceInstance)) {
			syncDeleteInstance(serviceInstance, platformService);
		} else {
			asyncDeleteInstance(instanceId, serviceInstance, platformService);
		}

	}

	protected abstract boolean isSyncPossibleOnDelete(ServiceInstance instance);

	@Async
	private void asyncDeleteInstance(String instanceId, ServiceInstance serviceInstance,
			PlatformService platformService) {
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
			throws ServiceBrokerException {
		preDeprovisionServiceInstance(serviceInstance);

		platformService.deleteInstance(serviceInstance);
	}

	protected abstract void preDeprovisionServiceInstance(ServiceInstance serviceInstance);

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			String serviceDefinitionId, String planId, String appGuid)
					throws ServiceInstanceBindingExistsException, ServiceBrokerException,
					ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException {
		validateServiceId(serviceDefinitionId);

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance = getServiceInstance(instanceId);

		Plan plan = getPlan(serviceDefinition, planId);

		ServiceInstanceBindingResponse response = bindService(serviceInstance, plan);

		internalBindingIdMapping.put(bindingId, serviceInstance.getId());

		return response;
	}

	private void validateBindingNotExists(String bindingId, String instanceId)
			throws ServiceInstanceBindingExistsException {
		if (internalBindingIdMapping.containsKey(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}
	}

	protected abstract ServiceInstanceBindingResponse bindService(ServiceInstance instance, Plan plan)
			throws ServiceBrokerException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.ServiceInstanceBindingService#
	 * deleteServiceInstanceBinding(java.lang.String)
	 */
	@Override
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException {
		ServiceInstance serviceInstance = getBinding(bindingId);
		deleteBinding(serviceInstance);
	}

	private ServiceInstance getBinding(String bindingId) throws ServerviceInstanceBindingDoesNotExistsException {
		String serviceInstanceId = internalBindingIdMapping.get(bindingId);
		if (serviceInstanceId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		return getServiceInstance(serviceInstanceId);
	}

	protected abstract void deleteBinding(ServiceInstance serviceInstanceId) throws ServiceBrokerException;

}
