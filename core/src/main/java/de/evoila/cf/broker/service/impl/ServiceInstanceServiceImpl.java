/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.ServiceInstanceCreationResult;
import de.evoila.cf.broker.service.ServiceInstanceFactory;

/**
 * @author Christian
 *
 */
public abstract class ServiceInstanceServiceImpl implements ServiceInstanceFactory {

	private Map<String, ServiceInstance> serviceInstances = new ConcurrentHashMap<String, ServiceInstance>();

	private Map<String, String> internalBindingIdMapping = new ConcurrentHashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#createServiceInstance(
	 * de.evoila.cf.broker.model.ServiceDefinition, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition service, String serviceInstanceId, String planId,
			String organizationGuid, String spaceGuid) throws ServiceInstanceExistsException, ServiceBrokerException {

		if (serviceInstances.containsKey(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, service.getId());
		}
		ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceId, service.getId(), planId, organizationGuid,
				spaceGuid);

		ServiceInstanceCreationResult creationResult = provisionServiceInstance(serviceInstanceId, planId);
		if (creationResult.getInternalId() != null)
			serviceInstances.put(serviceInstanceId, serviceInstance);

		return serviceInstance;
	}

	 /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#getServiceInstance(
	 * java.lang.String)
	 */
	 public ServiceInstance getServiceInstance(String id) {
		 return this.serviceInstances.get(id); 
	 }

	protected abstract ServiceInstanceCreationResult provisionServiceInstance(String serviceInstanceId, String planId)
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
	public ServiceInstance deleteServiceInstance(String instanceId)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		ServiceInstance serviceInstance = getServiceInstance(instanceId);

		deprovisionServiceInstance(serviceInstance.getInternalId());
		
		return serviceInstance;
	}

	protected abstract void deprovisionServiceInstance(String internalId);

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			String serviceId, String planId, String appGuid) throws ServiceInstanceBindingExistsException,
					ServiceBrokerException, ServiceInstanceDoesNotExistException {
		String internalId = getInternalInstance(instanceId);

		validateBindingNotExists(bindingId, instanceId);

		ServiceInstanceBindingResponse response = bindService(internalId);

		internalBindingIdMapping.put(bindingId, internalId);

		return response;
	}

	private void validateBindingNotExists(String bindingId, String instanceId)
			throws ServiceInstanceBindingExistsException {
		if (internalBindingIdMapping.containsKey(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}
	}

	private String getInternalInstance(String instanceId) throws ServiceInstanceDoesNotExistException {
		String internalId = this.getInternalId(instanceId);
		if (internalId == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		return internalId;
	}

	protected abstract ServiceInstanceBindingResponse bindService(String internalId) throws ServiceBrokerException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.ServiceInstanceBindingService#
	 * deleteServiceInstanceBinding(java.lang.String)
	 */
	@Override
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException {
		String internalId = getBinding(bindingId);
		deleteBinding(internalId);
	}

	private String getBinding(String bindingId) throws ServerviceInstanceBindingDoesNotExistsException {
		String internalId = internalBindingIdMapping.get(bindingId);
		if (internalId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		return internalId;
	}

	protected abstract void deleteBinding(String internalId) throws ServiceBrokerException;

}
