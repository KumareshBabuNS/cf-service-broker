/**
 * 
 */
package de.evoila.cf.cpi.custom.props;

import java.util.Map;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Johannes Hiemer.
 *
 */
public class MongoDBCustomPropertyHandler implements DomainBasedCustomPropertyHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.cpi.openstack.custom.props.DomainBasedCustomPropertyHandler#
	 * addDomainBasedCustomProperties(de.evoila.cf.broker.model.Plan,
	 * java.util.Map, java.lang.String)
	 */
	@Override
	public Map<String, String> addDomainBasedCustomProperties(Plan plan, Map<String, String> customProperties,
			ServiceInstance serviceInstance) {
		Object replicaSetOptional = plan.getMetadata().get("replicaSet");

		if (replicaSetOptional != null && replicaSetOptional instanceof String) {
			String replicaSet = (String) replicaSetOptional;

			customProperties.put("replicaSet", replicaSet);
		}

		return customProperties;
	}

}
