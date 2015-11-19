/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
public class LogstashBindingService extends BindingServiceImpl {
	
	@Resource(name = "customProperties")
	public Map<String, String> customProperties;

	private Logger log = LoggerFactory.getLogger(getClass());

	public void create(ServiceInstance serviceInstance, Plan plan) {
		log.debug("No need to implement that for logstash");
	}

	public void delete(ServiceInstance serviceInstance, Plan plan) {
		log.debug("No need to implement that for logstash");
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		log.debug("bind Service");

		String url = String.format("syslog://%s:%s", serviceInstance.getHost(), customProperties.get("ls_port"));

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", url);
		
		ServiceInstanceBindingResponse serviceInstanceBindingResponse = new ServiceInstanceBindingResponse(credentials, url);

		return serviceInstanceBindingResponse;
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		log.debug("No need to implement that for logstash");
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

}
