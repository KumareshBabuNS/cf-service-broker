/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DB;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceExistsException;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.mongodb.repositories.MongoServiceInstanceRepository;
import de.evoila.cf.broker.service.ServiceInstanceService;
import de.evoila.cf.broker.service.impl.mongodb.MongoDBAdminService;

/**
 * @author Johannes Hiemer
 *
 */
@Service
public class MongoDBServiceInstanceService implements ServiceInstanceService {

	@Autowired
	private MongoServiceInstanceRepository mongoServiceInstanceRepository;
	
	@Autowired
	private MongoDBAdminService mongoDBAdminService;
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
	    for (ServiceInstance serviceInstance : mongoServiceInstanceRepository.findAll()) {
	    	serviceInstances.add(serviceInstance);
	    }
	    
	    return serviceInstances;
	}

	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition service,
			String serviceInstanceId, String planId, String organizationGuid,
			String spaceGuid) throws ServiceInstanceExistsException,
			ServiceBrokerException {
		
		ServiceInstance instance = mongoServiceInstanceRepository.findOne(serviceInstanceId);
		if (instance != null) {
			throw new ServiceInstanceExistsException(instance);
		}
		
		instance = new ServiceInstance(serviceInstanceId, service.getId(),
				planId, organizationGuid, spaceGuid, null);
		
		if (mongoDBAdminService.databaseExists(instance.getId())) {
			mongoDBAdminService.deleteDatabase(instance.getId());
		}
		
		DB db = mongoDBAdminService.createDatabase(instance.getId());
		if (db == null) {
			throw new ServiceBrokerException("Failed to create new DB instance: " + instance.getId());
		}
		mongoServiceInstanceRepository.save(instance);
		return instance;
	}

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return mongoServiceInstanceRepository.findOne(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) throws ServiceBrokerException {
		mongoDBAdminService.deleteDatabase(id);
		ServiceInstance instance = mongoServiceInstanceRepository.findOne(id);
		mongoServiceInstanceRepository.delete(id);
		
		return instance;
	}

}
