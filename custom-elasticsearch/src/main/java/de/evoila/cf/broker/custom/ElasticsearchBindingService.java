/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
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
public class ElasticsearchBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	public void create(ServiceInstance serviceInstance, Plan plan) {
		log.debug("created Binding");
		// TODO
	}

	public void delete(ServiceInstance serviceInstance, Plan plan) {
		log.debug("deleted Binding");
		// TODO
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		log.debug("bind Service");

		String dbURL = String.format("http://%s:%d/%s", serviceInstance.getHost(), serviceInstance.getPort(),
				serviceInstance.getId());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		log.debug("unbind Service");
		// TODO
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new NotImplementedException();
	}

}
