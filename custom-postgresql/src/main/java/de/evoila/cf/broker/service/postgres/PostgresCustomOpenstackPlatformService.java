/**
 * 
 */
package de.evoila.cf.broker.service.postgres;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.openstack.custom.OpenstackPlatformService;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Service
public class PostgresCustomOpenstackPlatformService extends OpenstackPlatformService {

	Logger log = LoggerFactory.getLogger(PostgresCustomOpenstackPlatformService.class);

	/**
	 * 
	 */
	public PostgresCustomOpenstackPlatformService() {
		super();
	}

	@PostConstruct
	@Override
	public void registerCustomPlatformServie() {
		super.registerCustomPlatformServie();
	}

	@Override
	public ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		return super.postProvisioning(serviceInstance, plan);
	}

}
