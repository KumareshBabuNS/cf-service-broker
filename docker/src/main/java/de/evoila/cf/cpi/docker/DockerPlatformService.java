package de.evoila.cf.cpi.docker;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.command.CreateContainerResponse;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;

/**
 * 
 * @author Dennis Mueller.
 *
 */
@Service
public class DockerPlatformService extends DockerServiceFactory {

	@Autowired
	private PlatformRepository platformRepository;

	@Override
	@PostConstruct
	public void registerCustomPlatformServie() {
		platformRepository.addPlatform(Platform.DOCKER, this);
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
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
		boolean available = false;
		try {
			available = ServicePortAvailabilityVerifier
					.verifyServiceAvailability(serviceInstance.getHost(), serviceInstance.getPort());
		} catch (Exception e) {
			throw new PlatformException(
					"Service instance is not reachable. Service may not be started on instance.", e);
		}

		if (!available) {
			throw new PlatformException(
					"Service instance is not reachable. Service may not be started on instance.");
		}

		return serviceInstance;
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
			throws PlatformException {
		this.removeDockerContainer(serviceInstance.getInternalId());
	}

	@Override
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan) {
		return null;
	}

	@Override
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan, Map<String, String> customProperties) throws PlatformException {
		String instanceId = serviceInstance.getId();
		CreateContainerResponse container = this.createDockerContainer(instanceId, plan.getVolumeSize(), 
				customProperties);
		
		Map<String, Object> credentials  = containerCredentialMap.get(container.getId());
		String host = (String) credentials.get("host");
		int port = (int) credentials.get("port");
		
		serviceInstance = new ServiceInstance(serviceInstance, 
				"http://currently.not/available", container.getId(), host, port);
		
		return serviceInstance;
	}

}
