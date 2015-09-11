package de.evoila.cf.cpi.docker;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlattformRepository;

@Service
public class DockerPlatformService extends DockerServiceFactory {

	@Autowired
	private PlattformRepository plattformRepository;

	@PostConstruct
	@Override
	public void registerCustomPlatformServie() {
		plattformRepository.addPlatform(Platform.DOCKER, this);
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
		return new ServiceInstance(serviceInstance, serviceInstance.getDashboardUrl(), serviceInstance.getInternalId());
	}

	@Override
	public void preDeprovisionServiceInstance(ServiceInstance serviceInstance) {
	}

	@Override
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {
		return new ServiceInstance(instance, null, null);
	}

	@Override
	public void deleteServiceInstance(ServiceInstance serviceInstance)
			throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		// TODO Auto-generated method stub

	}

	@Override
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan) {
		return null;
	}

	@Override
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan) {
		String instanceId = serviceInstance.getId();
		String vhost = instanceId;
		String username = instanceId;
		String password = instanceId;
		String internalId = this.createDockerContainer(instanceId, plan.getVolumeSize(), vhost, username, password)
				.getId();
		return new ServiceInstance(serviceInstance, "http://currently.not/available", internalId);

	}

}
