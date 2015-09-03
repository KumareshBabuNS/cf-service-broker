/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.ServiceInstanceBindingService;
import de.evoila.cf.broker.service.ServiceInstanceService;

/**
 * @author Christian Brinker, evoila.
 *
 */
/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract class ServiceInstanceBindingServiceImpl implements ServiceInstanceBindingService {

	@Autowired
	private ServiceInstanceService serviceInstanceService;

	private ConcurrentHashMap<String, String> internalBindingIdMapping = new ConcurrentHashMap<String, String>();

	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			String serviceId, String planId, String appGuid) throws ServiceInstanceBindingExistsException,
					ServiceBrokerException, ServiceInstanceDoesNotExistException {
		String internalId = serviceInstanceService.getInternalId(instanceId);
		if (internalId == null) {
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}

		if (internalBindingIdMapping.containsKey(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}

		ServiceInstanceBindingResponse response = bindService(planId);

		internalBindingIdMapping.put(bindingId, internalId);

		return response;
	}

	abstract ServiceInstanceBindingResponse bindService(String planId) throws ServiceBrokerException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.ServiceInstanceBindingService#
	 * deleteServiceInstanceBinding(java.lang.String)
	 */
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException {
		String internalId = internalBindingIdMapping.get(bindingId);
		if (internalId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		deleteBinding(internalId);
	}

	abstract void deleteBinding(String internalId) throws ServiceBrokerException;

}
