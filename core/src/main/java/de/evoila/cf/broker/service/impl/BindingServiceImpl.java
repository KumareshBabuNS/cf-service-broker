/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.BindingService;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public abstract class BindingServiceImpl extends BaseServiceImpl implements BindingService {
	
	protected abstract ServiceInstanceBindingResponse bindService(ServiceInstance instance, Plan plan)
			throws ServiceBrokerException;
	
	protected abstract void deleteBinding(ServiceInstance serviceInstance) throws ServiceBrokerException;

	@Override
	public ServiceInstanceBindingResponse createServiceInstanceBinding(
			String bindingId, String instanceId, String serviceId, String planId, String appGuid)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException,
			ServiceDefinitionDoesNotExistException {
		
		validateBindingNotExists(bindingId, instanceId);

		ServiceInstance serviceInstance = getServiceInstance(instanceId);

		Plan plan = getPlan(serviceDefinition, planId);

		ServiceInstanceBindingResponse response = bindService(serviceInstance, plan);

		internalBindingIdMapping.put(bindingId, serviceInstance.getId());

		return response;
	}

	@Override
	public void deleteServiceInstanceBinding(String bindingId)
			throws ServiceBrokerException,
			ServerviceInstanceBindingDoesNotExistsException {
		ServiceInstance serviceInstance = getBinding(bindingId);
		deleteBinding(serviceInstance);
	}
	
	private void validateBindingNotExists(String bindingId, String instanceId)
			throws ServiceInstanceBindingExistsException {
		if (internalBindingIdMapping.containsKey(bindingId)) {
			throw new ServiceInstanceBindingExistsException(bindingId, instanceId);
		}
	}

	private ServiceInstance getBinding(String bindingId) throws ServerviceInstanceBindingDoesNotExistsException {
		String serviceInstanceId = internalBindingIdMapping.get(bindingId);
		if (serviceInstanceId == null) {
			throw new ServerviceInstanceBindingDoesNotExistsException(bindingId);
		}
		return getServiceInstance(serviceInstanceId);
	}

}
