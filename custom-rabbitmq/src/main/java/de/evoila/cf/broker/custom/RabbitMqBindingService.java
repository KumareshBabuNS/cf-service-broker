/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.custom.mongodb.RabbitMqService;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class RabbitMqBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitMqService rabbitMqService;

	private boolean connection(ServiceInstance serviceInstance) throws IOException {
		if (rabbitMqService.isConnected())
			return true;
		else {
			log.info("Opening connection to " + serviceInstance.getHost() + 
					":" + serviceInstance.getPort());
			rabbitMqService.createConnection(serviceInstance.getId(), 
					serviceInstance.getHost(), serviceInstance.getPort());
		}
		return true;
	}

	public void create(ServiceInstance serviceInstance, Plan plan) throws IOException {
		connection(serviceInstance);
		
		String instanceId = serviceInstance.getId();
		
	}
	
	public void delete(ServiceInstance serviceInstance, Plan plan) throws IOException {
		connection(serviceInstance);
		
		String instanceId = serviceInstance.getId();
		
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, 
			Plan plan) throws ServiceBrokerException {
		
		try {
			connection(serviceInstance);
		} catch (IOException e) {
			throw new ServiceBrokerException("Could not open RabbitMQ connection", e);
		}
		
		SecureRandom random = new SecureRandom();
        String password = new BigInteger(130, random).toString(32);
		
        // Use vHost and Create User vHost needs to be created by Thomas
        
		String dbURL = String.format("postgres://%s:%s@%s:%d/%s", serviceInstance.getId(), password,
				rabbitMqService.getHost(), rabbitMqService.getPort(), serviceInstance.getId());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		try {
			connection(serviceInstance);
		} catch (IOException e) {
			throw new ServiceBrokerException("Could not open RabbitMQ connection", e);
		}

		// Delete User from vHost
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new NotImplementedException();
	}

}
