package de.evoila.cf.broker.service;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;

/**
 * Handles bindings to service instances.
 * 
 * @author sgreenberg@gopivotal.com
 */
public interface ServiceInstanceBindingService {

	/**
	 * Create a new binding to a service instance.
	 * 
	 * @param bindingId
	 *            The id provided by the cloud controller
	 * @param instanceId
	 *            The id of the service instance
	 * @param serviceId
	 *            The id of the service
	 * @param planId
	 *            The plan used for this binding
	 * @param appGuid
	 *            The guid of the app for the binding
	 * @return
	 * @throws ServiceInstanceBindingExistsException
	 *             if the same binding already exists.
	 * @throws ServiceBrokerException
	 * @throws ServiceInstanceDoesNotExistException
	 */
	public ServiceInstanceBindingResponse createServiceInstanceBinding(String bindingId, String instanceId,
			String serviceId, String planId, String appGuid) throws ServiceInstanceBindingExistsException,
					ServiceBrokerException, ServiceInstanceDoesNotExistException;

	// /**
	// * @param id
	// * @return The ServiceInstanceBinding or null if one does not exist.
	// */
	// ServiceInstanceBinding getServiceInstanceBinding(String id);

	/**
	 * Delete the service instance binding. If a binding doesn't exist, return
	 * null.
	 * 
	 * @param id
	 * @throws ServiceBrokerException
	 * @throws ServerviceInstanceBindingDoesNotExistsException
	 */
	void deleteServiceInstanceBinding(String id)
			throws ServiceBrokerException, ServerviceInstanceBindingDoesNotExistsException;

}
