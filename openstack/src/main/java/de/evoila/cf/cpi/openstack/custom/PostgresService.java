/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.ServiceInstanceCreationResult;
import de.evoila.cf.cpi.openstack.OpenstackServiceFactory;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Service("service")
public class PostgresService extends OpenstackServiceFactory {

	@Override
	protected ServiceInstanceCreationResult provisionServiceInstance(
			String serviceInstanceId, String planId)
			throws ServiceBrokerException {
		return null;
	}

	@Override
	protected void deprovisionServiceInstance(String internalId) {
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String internalId)
			throws ServiceBrokerException {
		return null;
	}

	@Override
	protected void deleteBinding(String internalId)
			throws ServiceBrokerException {
	}

	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		throw new NotImplementedException("Currently not supported");
	}	

}
