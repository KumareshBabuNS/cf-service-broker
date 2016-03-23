/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
 *
 */
/**
 * @author Christian Brinker, evoila.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#createCredentials(
	 * java.lang.String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.ServerAddress)
	 */
	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			ServerAddress host) throws ServiceBrokerException {

		String url = String.format("syslog://%s:%s", host.getIp(), host.getPort());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", url);

		return credentials;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindServiceKey(java.
	 * lang.String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.Plan, java.util.List)
	 */
	@Override
	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstance serviceInstance, Plan plan,
			List<ServerAddress> externalAddresses) throws ServiceBrokerException {

		log.debug("bind service key");

		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, externalAddresses.get(0));

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials, (String) credentials.get("uri"));
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindService(java.lang
	 * .String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.Plan)
	 */
	@Override
	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		log.debug("bind service");

		ServerAddress host = serviceInstance.getHosts().get(0);
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, host);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials,
				(String) credentials.get("uri"));
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
