/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.Map;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract interface PlatformService {
	
	/**
	 * @return All known ServiceInstances
	 */
	// List<ServiceInstance> getAllServiceInstances();
	/**
	 * @param id
	 * @return The ServiceInstance with the given id or null if one does not
	 *         exist
	 */
	// ServiceInstance getServiceInstance(String id);
	
	public void registerCustomPlatformServie();
	
	/**
	 * @param plan
	 * @return
	 */
	public boolean isSyncPossibleOnCreate(Plan plan);

	/**
	 * @param plan
	 * @return
	 */
	public boolean isSyncPossibleOnDelete(ServiceInstance instance);

	/**
	 * @param plan
	 * @return
	 */
	public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan);
	
	/**
	 * 
	 * @param serviceInstance
	 * @param plan
	 * @return
	 * @throws ServiceBrokerException
	 */
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException;
	
	/**
	 * 
	 * @param serviceInstance
	 */
	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance);
	
	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 * @throws Exception 
	 */
	public ServiceInstance createInstance(ServiceInstance instance, Plan plan, Map<String, String> customParameters) throws Exception;

	/**
	 * Same result as in PlatformService.createInstance(), but without creating
	 * a ServiceInstance on the platform. Used to provide information during
	 * asynchronous operations
	 * 
	 * @param instance
	 * @param plan
	 * @return
	 */
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan);

	/**
	 * @param instance
	 * @throws ServiceInstanceDoesNotExistException 
	 * @throws ServiceBrokerException 
	 */
	public void deleteServiceInstance(ServiceInstance serviceInstance) throws ServiceBrokerException, ServiceInstanceDoesNotExistException;

	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan);

}
