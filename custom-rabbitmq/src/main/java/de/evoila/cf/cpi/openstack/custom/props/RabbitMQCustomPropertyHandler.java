/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom.props;

import java.util.Map;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class RabbitMQCustomPropertyHandler implements DomainBasedCustomPropertyHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.cpi.openstack.custom.props.DomainBasedCustomPropertyHandler#
	 * addDomainBasedCustomProperties(de.evoila.cf.broker.model.Plan,
	 * java.util.Map, java.lang.String)
	 */
	@Override
	public void addDomainBasedCustomProperties(Plan plan, Map<String, String> customParameters,
			ServiceInstance serviceInstance) {
		String id = serviceInstance.getId();
		customParameters.put("rabbit_vhost", id);

		// TODO add secure user/pass-combination
		customParameters.put("rabbit_user", "evoila");
		customParameters.put("rabbit_passw", "evoila");
	}
}
