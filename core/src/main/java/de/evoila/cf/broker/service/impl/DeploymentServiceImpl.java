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
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.JobProgress;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceResponse;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;
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
	private PlatformRepository platformRepository;

	@Autowired
	private ServiceDefinitionRepository serviceDefinitionRepository;

	@Autowired
	private ServiceInstanceRepository serviceInstanceRepository;

	@Autowired(required = false)
	private AsyncDeploymentService asyncDeploymentService;

	@Override
	public JobProgress getLastOperation(String serviceInstanceId)
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
		String progress = asyncDeploymentService.getProgress(serviceInstanceId);
		if (progress == null || !serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}
		return new JobProgress(progress, "");
	}

	@Override
	public ServiceInstanceResponse createServiceInstance(String serviceInstanceId, String serviceDefinitionId,
			String planId, String organizationGuid, String spaceGuid, Map<String, String> parameters)
					throws ServiceInstanceExistsException, ServiceBrokerException,
					ServiceDefinitionDoesNotExistException {

		serviceDefinitionRepository.validateServiceId(serviceDefinitionId);

		if (serviceInstanceRepository.containsServiceInstanceId(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId,
					serviceDefinitionRepository.getServiceDefinition().getId());
		}

		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId,
				serviceDefinitionRepository.getServiceDefinition().getId(), planId, organizationGuid, spaceGuid,
				parameters == null ? new ConcurrentHashMap<String, String>()
						: new ConcurrentHashMap<String, String>(parameters));

		Plan plan = serviceDefinitionRepository.getPlan(planId);

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (platformService.isSyncPossibleOnCreate(plan)) {
			return syncCreateInstance(serviceInstance, plan, platformService);
		} else {
			ServiceInstanceResponse serviceInstanceResponse = new ServiceInstanceResponse(serviceInstance, true);

			serviceInstanceRepository.addServiceInstance(serviceInstance.getId(), serviceInstance);

			asyncDeploymentService.asyncCreateInstance(this, serviceInstance, plan, platformService);

			return serviceInstanceResponse;
		}
	}

	ServiceInstanceResponse syncCreateInstance(ServiceInstance serviceInstance, Plan plan,
			PlatformService platformService) throws ServiceBrokerException {
		ServiceInstance createdServiceInstance;
		try {
			createdServiceInstance = platformService.createInstance(serviceInstance, plan);
		} catch (Exception e) {
			throw new ServiceBrokerException("Could not create instance due to: " + e.getMessage());
		}

		createdServiceInstance = platformService.postProvisioning(createdServiceInstance, plan);
		if (createdServiceInstance.getInternalId() != null)
			serviceInstanceRepository.addServiceInstance(createdServiceInstance.getId(), createdServiceInstance);
		else {
			throw new ServiceBrokerException(
					"Internal error. Service instance was not created. ID was: " + serviceInstance.getId());
		}
		return new ServiceInstanceResponse(createdServiceInstance, false);
	}

	/**
	 * 
	 * @param instanceId
	 * @return
	 */
	protected String getInternalId(String instanceId) {
		return serviceInstanceRepository.getServiceInstance(instanceId).getInternalId();
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
		ServiceInstance serviceInstance = serviceInstanceRepository.getServiceInstance(instanceId);

		if (serviceInstance == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		Plan plan = serviceDefinitionRepository.getPlan(serviceInstance.getPlanId());

		PlatformService platformService = platformRepository.getPlatformService(plan.getPlatform());

		if (platformService.isSyncPossibleOnDelete(serviceInstance)
				&& platformService.isSyncPossibleOnDelete(serviceInstance)) {
			syncDeleteInstance(serviceInstance, platformService);
		} else {
			asyncDeploymentService.asyncDeleteInstance(this, instanceId, serviceInstance, platformService);
		}

	}

	void syncDeleteInstance(ServiceInstance serviceInstance, PlatformService platformService)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		platformService.preDeprovisionServiceInstance(serviceInstance);

		platformService.deleteServiceInstance(serviceInstance);

		serviceInstanceRepository.deleteServiceInstance(serviceInstance.getId());
	}
}
