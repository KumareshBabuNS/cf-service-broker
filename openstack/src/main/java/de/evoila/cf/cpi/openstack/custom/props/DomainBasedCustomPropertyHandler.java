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
public interface DomainBasedCustomPropertyHandler {
	public void addDomainBasedCustomProperties(Plan plan, Map<String, String> customParameters,
			ServiceInstance serviceInstance);
}
