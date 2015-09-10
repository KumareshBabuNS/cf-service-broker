/**
 * 
 */
package de.evoila.cf.cpi.openstack;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openstack4j.model.compute.Server;
import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import de.evoila.cf.cpi.openstack.fluent.NovaFluent;
import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class OpenstackServiceFactory implements PlatformService {
	
	private static final String CREATE_IN_PROGRESS = "CREATE_IN_PROGRESS";

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private HeatFluent heatFluent;
	
	@Autowired
	private NovaFluent novaFluent;
	
	@Value("${openstack.endpoint}")
	private String endpoint;

	@Value("${openstack.username}")
	private String username;

	@Value("${openstack.password}")
	private String password;

	@Value("${openstack.tenant}")
	private String tenant;
	
	@Value("${openstack.networkId}")
	private String networkId;
	
	@Value("${openstack.subnetId}")
	private String subnetId;
	
	@Value("${openstack.subnet}")
	private String subnet;
	
	@Value("${openstack.imageId}")
	private String imageId;
	
	@Value("${openstack.keypair}")
	private String keypair;
	
	@Value("${openstack.cinder.az}")
	private String availabilityZone;
	
	@Value("${backend.default.port}")
	protected int defaultPort;
	
	private String heatTemplate;
	
	private static String DEFAULT_ENCODING = "UTF-8";
	
	private static boolean DEFAULT_DISABLE_ROLLBACK = true;
	
	private static long DEFAULT_TIMEOUT_MINUTES = 10;
	
	@PostConstruct
	public void initialize() {
		log.debug("Initializing Openstack Connection Factory");
		
		OpenstackConnectionFactory.getInstance()
			.setCredential(username, password)
			.authenticate(endpoint, tenant);
		
		log.debug("Reading heat template definition for openstack");
		
		URL url = this.getClass().getResource("/openstack/template.yml");
		
		try {
			heatTemplate = this.readTemplateFile(url);
		} catch (IOException | URISyntaxException e) {
			log.info("Failed to load heat template", e);
		}
		
		Assert.notNull(url, "Heat template definition must be provided.");
	}
	
	private String readTemplateFile(URL url) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(url.toURI()));
		return new String(encoded, DEFAULT_ENCODING);
	}
	
	protected Stack create(String instanceId, Map<String, String> customParameters) throws InterruptedException {
		Map<String, String> completeParameters = new HashMap<String, String>();
		completeParameters.putAll(defaultParameters());
		completeParameters.putAll(customParameters);
		
		String name = uniqueName(instanceId);
		
		Stack stack = heatFluent.create(name, 
				heatTemplate, 
				completeParameters, 
				DEFAULT_DISABLE_ROLLBACK, 
				DEFAULT_TIMEOUT_MINUTES);
		
		Thread.sleep(5000);
		
		stack = heatFluent.get(name);
		while (stack.getStatus().equals(CREATE_IN_PROGRESS)) {
			stack = heatFluent.get(name);
			
			Thread.sleep(5000);
		}
		
		return stack;
	}
	
	protected void delete(String instanceId) {
		Stack stack = heatFluent.get(uniqueName(instanceId));
		
		heatFluent.delete(stack.getName(), stack.getId());
	}
	
	protected Server details(String instanceId) {
		List<Server> servers = servers(instanceId);
		
		if (servers.size() == 1) 
			return servers.get(0);
		else
			return null;
	}
	
	protected boolean verifyServiceAvailability(String instanceId) {
		boolean available = false;
		
		for (Server server : servers(instanceId)) {
			available = ServicePortAvailabilityVerifier.execute(novaFluent.ip(server, subnet), 51111);
			
			log.info("Service Port availability: " + available);
		}
		
		return available;
	}
	
	private List<Server> servers(String instanceId) {
		Stack stack = heatFluent.get(uniqueName(instanceId));
		
		return heatFluent.servers(stack.getName(), stack.getId(), HeatFluent.NOVA_INSTANCE_TYPE);
	}
	
	protected String primaryIp(String instanceId) {
		return novaFluent.ip(this.details(instanceId), instanceId);
	}
	
	protected String uniqueName(String instanceId) {
		return "s" + instanceId;
	}
	
	private Map<String, String> defaultParameters() {
		Map<String, String> defaultParameters = new HashMap<String, String>();
		defaultParameters.put("image_id", imageId);
		defaultParameters.put("keypair", keypair);
		defaultParameters.put("network_id", networkId);
		defaultParameters.put("subnet_id", subnetId);
		defaultParameters.put("availability_zone", availabilityZone);
		
		return defaultParameters;
	}

}
