/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.List;
import java.util.Map;

import org.openstack4j.model.heat.Stack;
import org.springframework.beans.factory.annotation.Autowired;

import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.cpi.openstack.OpenstackServiceFactory;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class MongoIpAccessor extends CustomIpAccessor {

	private HeatFluent heatFluent;

	@Override
	public List<ServerAddress> getIpAddresses(String instanceId) {
		Stack stack = heatFluent.get(OpenstackServiceFactory.uniqueName(instanceId));
		List<Map<String, Object>> outputs = stack.getOutputs();
		for (Map<String, Object> output : outputs) {
			Object outputKey = output.get("output_key");
			if (outputKey != null && outputKey instanceof String) {
				String key = (String) outputKey;
				if (key.equals("ips")) {
					String outputValue = (String) output.get("output_value");

					final List<String> ips = Lists.newArrayList(outputValue.split(","));
					List<ServerAddress> serverAddresses = Lists.newArrayList();
					for (int i = 0; i < ips.size(); i++) {
						serverAddresses.add(new ServerAddress(Integer.toString(i), ips.get(i)));
					}
					return serverAddresses;
				}
			}
		}

		return Lists.newArrayList();
	}

	@Autowired
	public void setHeatFluent(HeatFluent heatFluent) {
		this.heatFluent = heatFluent;
	}
}
