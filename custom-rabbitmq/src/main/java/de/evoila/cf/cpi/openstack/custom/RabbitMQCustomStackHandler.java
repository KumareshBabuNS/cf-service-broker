/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.Map;

import org.openstack4j.model.heat.Stack;
import org.springframework.beans.factory.annotation.Value;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;

/**
 * @author Christian Mueller, evoila
 *
 */
public class RabbitMQCustomStackHandler extends CustomStackHandler {
	
	@Value("${openstack.log_port}")
	private String logPort;
	
	@Value("${openstack.log_host}")
	private String logHost;
	
	public Stack create(String instanceId, Map<String, String> customParameters)
			throws PlatformException {
		
		if (customParameters.containsKey("cluster")) {
			return createCluster(instanceId, customParameters);
		} 
		return super.create(instanceId, customParameters);
	}

	/**
	 * @param instanceId
	 * @param customParameters
	 * @param plan
	 * @return
	 */
	private Stack createCluster(String instanceId, Map<String, String> customParameters) {
		int numberSecondaries = customParameters..
		
		Integer numberPorts = numberSecondaries+1;
		Map<String, String> parametersPorts = copyProperties(customParameters)
		parametersPorts.put("secondary_number", numberPorts.toString());
		parametersPorts.put("network_id", "3e73c0d4-31a8-4cb5-a2f8-b12e4577395c");
		
		heatFluent.create(namePorts, templatePorts, parametersPorts , false, 10l);
		
		Stack stackPorts = stackProgressObserver.waitForStackCompletion(namePorts);	
		return null;
	}
	
	private  Map<String,String> copyProperties(Map<String,String> completeList, String... keys) {
		Map<String,String> copiedProps = new HashMap<>();
			
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			copiedProps.put(key, completeList.get(key));
		}
		return copiedProps;
	}
}
