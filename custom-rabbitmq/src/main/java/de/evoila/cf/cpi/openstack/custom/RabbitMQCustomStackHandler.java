/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;

/**
 * @author Christian Mueller, evoila
 *
 */
@Service
public class RabbitMQCustomStackHandler extends CustomStackHandler {
	
	/**
	 * 
	 */
	private static final String NETWORK_PORT = "network_port";
	private static final String SECONDARY_NUMBER = "secondary_number";
	private static final String PORT_NUMBER = "port_number";
	private static final String CLUSTER = "cluster";
	private static final String ERLANG_KEY = "erlang_key";
	private static final String RABBIT_PASSWORD = "rabbit_password";
	private static final String RABBIT_USER = "rabbit_user";
	private static final String RABBIT_VHOST = "rabbit_vhost";
	private static final String ETC_HOSTS = "etcHosts";
	private static final String MASTER_HOSTNAME = "hostname";
	private static final String SECONDARY_HOSTNAME = "secondaryHostname";
	private static final String VOLUME_SIZE = "volume_size";
	private static final String FLAVOR = "flavor";
	private static final String PORT_PRIM = "port_prim";
	

	@Value("${openstack.log_port}")
	private String logPort;
	
	@Value("${openstack.log_host}")
	private String logHost;
	
	private static final Logger log = LoggerFactory.getLogger(RabbitMQCustomStackHandler.class);
	
	@Override
	public void create(String instanceId, Map<String, String> customParameters)
			throws PlatformException, InterruptedException {
		
		if (customParameters.containsKey(CLUSTER)) {
			createCluster(instanceId, customParameters);
		} else
			super.create(instanceId, customParameters);
	}

	/**
	 * @param instanceId
	 * @param customParameters
	 * @param plan
	 * @return
	 * @throws PlatformException 
	 * @throws InterruptedException 
	 */
	private List<Stack> createCluster(String instanceId, Map<String, String> customParameters) throws PlatformException, InterruptedException {
		
		log.debug("Start create a rabbitMQ cluster");
		
		customParameters.putAll(defaultParameters());
		
		customParameters.put(LOG_PORT, logPort);
		customParameters.put(LOG_HOST, logHost);
		
		Integer secondaryNumber = 0;
		
		if (customParameters.containsKey(SECONDARY_NUMBER)) {
			 secondaryNumber = Integer.parseInt(customParameters.get(SECONDARY_NUMBER).toString());
			 Integer portNumber = secondaryNumber + 1;
			customParameters.put(PORT_NUMBER, portNumber.toString());
		}
		
		Map<String, String> parametersPorts = copyProperties(customParameters, 
				NETWORK_ID, PORT_NUMBER);
				
		String namePorts = "s"+instanceId+"_Ports";
		String templatePorts = accessTemplate("/openstack/templatePorts.yaml");;

		heatFluent.create(namePorts, templatePorts, parametersPorts , false, 10l);	
		
		Stack stackPorts = stackProgressObserver.waitForStackCompletion(namePorts);	
		
		List<String> ips = null;
		List<String> ports = null;
		
		for (Map<String, Object> output : stackPorts.getOutputs()) {
			Object outputKey = output.get("output_key");
			if (outputKey != null && outputKey instanceof String) {
				String key = (String) outputKey;
				if (key.equals("secondary_ips")) {	
					ips = (List<String>) output.get("output_value");

				}
				if (key.equals("secondary_ports")) {
					ports = (List<String>) output.get("output_value");
				}
			}
		}

		String primIp = ips.get(0);
		ips.remove(0);
		String primPort = ports.get(0);
		customParameters.put(PORT_PRIM, primPort);
		ports.remove(0);
		String primHostname = "p-"+primIp.replace(".", "-");
		
		String etcHosts = primIp+" "+primHostname+"\n";
		for (String secIp : ips) {
			etcHosts += secIp+" "+"sec-"+secIp.replace(".", "-")+"\n";
		}
		customParameters.put(MASTER_HOSTNAME, primHostname);
		customParameters.put(ETC_HOSTS, etcHosts);
		
		Map<String, String> parametersPrimary = copyProperties(customParameters, 
				RABBIT_VHOST, RABBIT_USER, RABBIT_PASSWORD, LOG_HOST, LOG_PORT, ERLANG_KEY, FLAVOR, VOLUME_SIZE,
				ETC_HOSTS, MASTER_HOSTNAME, PORT_PRIM, AVAILABILITY_ZONE, KEYPAIR, IMAGE_ID);
		
		
		String templatePrimary = accessTemplate("/openstack/templatePrim.yaml");
		String namePrimary = "s"+instanceId+"_primary";
		
		heatFluent.create(namePrimary, templatePrimary, parametersPrimary , false, 10l);
		
		stackProgressObserver.waitForStackCompletion(namePrimary);	
		
		
		Map<String, String> parametersSecondary = copyProperties(customParameters, 
				RABBIT_VHOST, RABBIT_USER, RABBIT_PASSWORD, LOG_HOST, LOG_PORT, ERLANG_KEY, FLAVOR, VOLUME_SIZE,
				ETC_HOSTS, MASTER_HOSTNAME, AVAILABILITY_ZONE, KEYPAIR, IMAGE_ID );		
		
		String templateSec = accessTemplate("/openstack/templateSecondaries.yaml");
		

		for (int i = 0; i < secondaryNumber; i++) {
			if ( i > 0 ) Thread.sleep(500);
			if (parametersSecondary.containsKey(NETWORK_PORT)) {
				parametersSecondary.remove(NETWORK_PORT);
			}
			parametersSecondary.put(NETWORK_PORT, ports.get(i));
			
			if (parametersSecondary.containsKey(SECONDARY_HOSTNAME)) {
				parametersSecondary.remove(SECONDARY_HOSTNAME);
			}
			parametersSecondary.put(SECONDARY_HOSTNAME, "sec-"+ips.get(i).replace(".", "-"));
			
			heatFluent.create("s"+instanceId+"_Sec"+i, templateSec, parametersSecondary, false, 10l);
						
		}
		
		List<Stack> stackSec = new ArrayList<Stack>();
		for (int i = 0; i < secondaryNumber; i++) {
			stackSec.add(stackProgressObserver.waitForStackCompletion("s"+instanceId+"_Sec"+i));	
		}
		
		log.debug("Stack deployment for RabbitMQ ready - Stacks:"+stackSec.size());
		
		return stackSec;
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
