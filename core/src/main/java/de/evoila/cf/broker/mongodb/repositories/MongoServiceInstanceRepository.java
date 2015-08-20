/**
 * 
 */
package de.evoila.cf.broker.mongodb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.evoila.cf.broker.model.ServiceInstance;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public abstract interface MongoServiceInstanceRepository extends CrudRepository<ServiceInstance, String>,
	PagingAndSortingRepository<ServiceInstance, String> {

}