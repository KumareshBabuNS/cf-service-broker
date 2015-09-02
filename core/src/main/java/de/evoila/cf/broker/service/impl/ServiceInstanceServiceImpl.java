/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceCreationResult;
import de.evoila.cf.broker.service.ServiceInstanceService;

/**
 * @author Christian
 *
 */
public abstract class ServiceInstanceServiceImpl implements ServiceInstanceService {

	private Map<String, String> internalIdMapping = new ConcurrentHashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#createServiceInstance(
	 * de.evoila.cf.broker.model.ServiceDefinition, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public String createServiceInstance(ServiceDefinition service, String serviceInstanceId, String planId,
			String organizationGuid, String spaceGuid) throws ServiceInstanceExistsException, ServiceBrokerException {

		if (internalIdMapping.containsKey(serviceInstanceId)) {
			throw new ServiceInstanceExistsException(serviceInstanceId, service.getId());
		}
		// create
		ServiceInstanceCreationResult creationResult = provisionServiceInstance(planId);

		internalIdMapping.put(serviceInstanceId, creationResult.getInternalId());

		return creationResult.getDaschboardUrl();
	}

	protected abstract ServiceInstanceCreationResult provisionServiceInstance(String planId)
			throws ServiceBrokerException;

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * de.evoila.cf.broker.service.ServiceInstanceService#getServiceInstance(
	// * java.lang.String)
	// */
	// public ServiceInstance getServiceInstance(String id) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#deleteServiceInstance(
	 * java.lang.String)
	 */
	public ServiceInstance deleteServiceInstance(String id) throws ServiceBrokerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.ServiceInstanceService#getInternalId(java.
	 * lang.String)
	 */
	public String getInternalId(String instanceId) {
		return this.internalIdMapping.get(instanceId);
	}
}
