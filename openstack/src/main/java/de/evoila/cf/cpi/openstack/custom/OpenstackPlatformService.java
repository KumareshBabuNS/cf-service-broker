/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.impl.DeploymentServiceImpl;
import de.evoila.cf.cpi.openstack.OpenstackServiceFactory;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Service
public class OpenstackPlatformService extends OpenstackServiceFactory implements PlatformService {
	
	@Autowired
	private DeploymentServiceImpl deploymentServiceImpl;
	
	@Override
	public void registerCustomPlatformServie() {
		deploymentServiceImpl.addPlatformService(Platform.OPENSTACK, this);
	}

	@Override
	public boolean isSyncPossibleOnCreate(Plan plan) {
		return false;
	}

	@Override
	public boolean isSyncPossibleOnDelete(ServiceInstance instance) {
		return false;
	}

	@Override
	public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan) {
		return false;
	}

	@Override
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance,
			Plan plan) throws ServiceBrokerException {
		return null;
	}

	
	@Override
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan) {
		Map<String, String> customParameters = new HashMap<String, String>();
		customParameters.put("flavor", plan.getFlavorId());
		customParameters.put("volume_size", String.valueOf(plan.getVolumeSize()));
		
		String instanceId = serviceInstance.getId();
		
		customParameters.put("database_name", instanceId);
		customParameters.put("database_user", instanceId);
		customParameters.put("database_password", instanceId);
		
		try {
			this.create(instanceId, customParameters);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return new ServiceInstance(serviceInstance, "http://currently.not/available", this.uniqueName(instanceId));
	}

	@Override
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {		
		return null;
	}
	
	@Override
	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance) {}

	@Override
	public void deleteServiceInstance(ServiceInstance serviceInstance)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		this.delete(serviceInstance.getId());
	}

	@Override
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan) {
		throw new NotImplementedException("Updating Service Instances is currently not supported");
	}

}
