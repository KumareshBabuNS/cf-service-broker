/**
 * 
 */
package de.evoila.cf.broker.service;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract interface PlatformService {
	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	public ServiceInstance createInstance(ServiceInstance instance, Plan plan);

	/**
	 * Same result as in PlatformService.createInstance(), but without creating
	 * a ServiceInstance on the platform. Used to provide information during
	 * asynchronous operations
	 * 
	 * @param instance
	 * @param plan
	 * @return
	 */
	public ServiceInstance getCreateInstancePromiss(ServiceInstance instance, Plan plan);

	/**
	 * @param instance
	 */
	public void deleteInstance(ServiceInstance instance);

	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan);

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
}
