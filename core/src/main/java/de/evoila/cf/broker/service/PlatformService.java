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
public interface PlatformService {
	/**
	 * @param instance
	 * @param plan
	 * @return new ServiceInstance with updated fields
	 */
	public ServiceInstance createInstance(ServiceInstance instance, Plan plan);

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
}
