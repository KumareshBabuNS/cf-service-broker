/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.cypher.internal.compiler.v2_2.ast.rewriters.getDegreeOptimizer;
import org.openstack4j.model.heat.Stack;
import org.springframework.beans.factory.annotation.Autowired;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.VolumeUnit;
import de.evoila.cf.cpi.BaseIntegrationTest;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author Johannes Hiemer.
 *
 */
public class OpenstackPlatformServiceClusterTest extends BaseIntegrationTest {
	
	private Plan plan;
	
	private ServiceInstance serviceInstance;
	
	@Autowired
	private OpenstackPlatformService openstackPlatformService;
	
	@Before
	public void before() {
		plan = new Plan("basic", "500 MB PostgreSQL DB Basic Instance",
				"The most basic PostgreSQL plan currently available. Providing"
			+ "500 MB of capcity in a PostgreSQL DB.", Platform.OPENSTACK, 25, VolumeUnit.M, "3", 10);
		
		serviceInstance = new ServiceInstance(UUID.randomUUID().toString(), 
				UUID.randomUUID().toString(), plan.getId(), 
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), 
				null, "http://currently.not/available");
		
		Assert.assertNotNull(serviceInstance);
	}
	
	@Test
	public void createCluster() throws IOException, URISyntaxException, PlatformException {
		String template = openstackPlatformService.accessTemplate("/openstack/templatePorts.yaml");
		String name = "testResourceGroupsForNeutronPorts"+UUID.randomUUID().toString();
		
/*		Map<String, String> parameters = openstackPlatformService.defaultParameters();
		parameters.put("database_name", "evoila");
		parameters.put("database_user", "evoila");
		parameters.put("database_password", "evoila");
		parameters.put("database_number", "1");
		parameters.put("database_key", "aGFuc3d1cnN0");
		parameters.put("log_host", "172.24.102.12");
		parameters.put("log_port", "5002");
		parameters.put("erlang_key", "thisisjustatest4usguysfromEVOILA");
		parameters.put("secondary_number", "1");
		parameters.put("flavor", "m1.small");
		parameters.put("volume_size", "2");
*/
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("secondary_number", "2");
		parameters.put("network_id", "3e73c0d4-31a8-4cb5-a2f8-b12e4577395c");
		
		openstackPlatformService.getHeatFluent()
				.create(name, template, parameters , false, 10l);
		
		Stack stackPorts = openstackPlatformService.waitForStackCompletion(name);	
		
		List<Map<String, Object>> outputs = stackPorts.getOutputs();
		
		List<String> ips = null;
		List<String> ports = null;
		
		for (Map<String, Object> output : outputs) {
			Object outputKey = output.get("output_key");
			if (outputKey != null && outputKey instanceof String) {
				String key = (String) outputKey;
				System.out.println("drin0-"+key);
				if (key.equals("secondary_ips")) {
					ips = (List<String>) output.get("output_value");

				}
				if (key.equals("secondary_ports")) {
					ports = (List<String>) output.get("output_value");
				}
			}
		}
		
		for (int i = 0; i < ips.size(); i++) {
			System.out.println("IP:"+ips.get(i)+"-PORT:"+ports.get(i));
		}
	
		
		Assert.assertNotNull(stackPorts);
	}
	
}
