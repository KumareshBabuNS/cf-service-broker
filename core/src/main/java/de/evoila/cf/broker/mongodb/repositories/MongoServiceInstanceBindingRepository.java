/**
 * 
 */
package de.evoila.cf.broker.mongodb.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.evoila.cf.broker.model.ServiceInstanceBinding;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public abstract interface MongoServiceInstanceBindingRepository extends CrudRepository<ServiceInstanceBinding, String>,
	PagingAndSortingRepository<ServiceInstanceBinding, String> {

}