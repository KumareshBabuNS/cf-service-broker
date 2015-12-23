/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.VolumeUnit;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.openstack.OpenstackServiceFactory;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "backend")
public class OpenstackPlatformService extends OpenstackServiceFactory {

	@Autowired
	private PlatformRepository platformRepositroy;

	@Override
	@PostConstruct
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
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
		
		boolean available;
		try {
			available = ServicePortAvailabilityVerifier
					.verifyServiceAvailability(this.primaryIp(serviceInstance.getId()), serviceInstance.getPort());
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
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan,
			Map<String, String> customProperties) throws PlatformException {
		String instanceId = serviceInstance.getId();

		Map<String, String> platformParameters = new HashMap<String, String>();
		platformParameters.put("flavor", plan.getFlavorId());
		platformParameters.put("volume_size", volumeSize(plan.getVolumeSize(), plan.getVolumeUnit()));


		platformParameters.putAll(customProperties);

		try {
			this.create(instanceId, platformParameters);
		} catch (Exception e) {
			throw new PlatformException(e);
		}

		serviceInstance = new ServiceInstance(serviceInstance, "http://currently.not/available", this.uniqueName(instanceId),
				this.primaryIp(instanceId), this.ports.get("default"));
		
		if (ports.size() > 1) {
			for (Entry<String, Integer> port : ports.entrySet())
				if (!port.getKey().equals("default"))
					serviceInstance.getParameters().put(port.getKey(), port.getValue().toString());
		}
			
		
		return serviceInstance;
	}

	private String volumeSize(int volumeSize, VolumeUnit volumeUnit) {
		if (volumeUnit.equals(VolumeUnit.M))
			throw new NotAcceptableException("Volumes in openstack may not be smaller than 1 GB");
		else if (volumeUnit.equals(VolumeUnit.G))
			return String.valueOf(volumeSize);
		else if (volumeUnit.equals(VolumeUnit.T))
			return String.valueOf(volumeSize * 1024);
		return String.valueOf(volumeSize);
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
			throws PlatformException {
		this.delete(serviceInstance.getInternalId());
	}

	@Override
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan) {
		throw new NotSupportedException("Updating Service Instances is currently not supported");
	}

}
