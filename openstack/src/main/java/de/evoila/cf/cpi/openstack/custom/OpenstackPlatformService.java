/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.cpi.openstack.OpenstackServiceFactory;
import de.evoila.cf.cpi.openstack.custom.exception.OpenstackPlatformException;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Service
public class OpenstackPlatformService extends OpenstackServiceFactory {

	@Autowired
	private PlatformRepository platformRepositroy;

	@PostConstruct
	@Override
	public void registerCustomPlatformServie() {
		platformRepositroy.addPlatform(Platform.OPENSTACK, this);
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
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		return serviceInstance;
	}

	@Override
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan) throws OpenstackPlatformException {
		Map<String, String> customParameters = new HashMap<String, String>();
		customParameters.put("flavor", plan.getFlavorId());
		customParameters.put("volume_size", String.valueOf(plan.getVolumeSize()));

		String instanceId = serviceInstance.getId();

		customParameters.put("database_name", instanceId);
		customParameters.put("database_user", instanceId);
		customParameters.put("database_password", instanceId);

		try {
			this.create(instanceId, customParameters);
		} catch (Exception e) {
			throw new OpenstackPlatformException(e);
		}

		return new ServiceInstance(serviceInstance, "http://currently.not/available", this.uniqueName(instanceId),
				this.primaryIp(instanceId), this.defaultPort);
	}

	@Override
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {
		return new ServiceInstance(instance, null, null);
	}

	@Override
	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance) {
	}

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
