/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.rabbitmq.client.Channel;

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

	private boolean connection(ServiceInstance serviceInstance) {
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

	public void create(ServiceInstance serviceInstance, Plan plan) {
		connection(serviceInstance);
		
		String instanceId = serviceInstance.getId();
		
		Channel channel = rabbitMqService.rabbitmqClient().createChannel();
		channel.queueDeclare(instanceId, true, false, false, null);
	}
	
	public void delete(ServiceInstance serviceInstance, Plan plan) {
		connection(serviceInstance);
		
		String instanceId = serviceInstance.getId();
		
		Channel channel = rabbitMqService.rabbitmqClient().createChannel();
		channel.queueDelete(instanceId);
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, 
			Plan plan) throws ServiceBrokerException {
		
		connection(serviceInstance);
		
		SecureRandom random = new SecureRandom();
        String password = new BigInteger(130, random).toString(32);
		
        Channel channel = rabbitMqService.rabbitmqClient().createChannel();
		channel.queueDeclare(instanceId, true, false, false, null);

		String dbURL = String.format("postgres://%s:%s@%s:%d/%s", serviceInstance.getId(), password,
				rabbitMqService.getHost(), rabbitMqService.getPort(), serviceInstance.getId());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		connection(serviceInstance);

		rabbitMqService.rabbitmqClient()
		 	.getDatabase(serviceInstance.getId())
		 	.runCommand(new BasicDBObject("dropUser", bindingId));
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new NotImplementedException();
	}

}
