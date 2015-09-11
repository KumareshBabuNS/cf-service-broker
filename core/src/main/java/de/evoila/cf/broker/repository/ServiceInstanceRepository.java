package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface ServiceInstanceRepository {

	// Depl + Bind
	ServiceInstance getServiceInstance(String instanceId);

	// Depl
	boolean containsServiceInstanceId(String serviceInstanceId);

	// Depl + PGBindingTest
	void addServiceInstance(String id, ServiceInstance serviceInstance);

}