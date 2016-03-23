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
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import de.evoila.cf.broker.cpi.endpoint.EndpointAvailabilityService;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.cpi.AvailabilityState;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class OpenstackServiceFactory implements PlatformService {

	private static final String CREATE_IN_PROGRESS = "CREATE_IN_PROGRESS";

	private static final String CREATE_FAILED = "CREATE_FAILED";

	private final static String OPENSTACK_SERVICE_KEY = "openstackFactoryService";

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private HeatFluent heatFluent;

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

	@Value("${openstack.imageId}")
	private String imageId;

	@Value("${openstack.keypair}")
	private String keypair;

	@Value("${openstack.cinder.az}")
	private String availabilityZone;

	protected Map<String, Integer> ports;

	public Map<String, Integer> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, Integer> ports) {
		this.ports = ports;
	}

	private String heatTemplate;

	private static String DEFAULT_ENCODING = "UTF-8";

	private static boolean DEFAULT_DISABLE_ROLLBACK = false;

	private static long DEFAULT_TIMEOUT_MINUTES = 10;

	@Autowired
	private EndpointAvailabilityService endpointAvailabilityService;

	@PostConstruct
	public void initialize() {
		log.debug("Initializing Openstack Connection Factory");
		try {
			if (endpointAvailabilityService.isAvailable(OPENSTACK_SERVICE_KEY)) {
				OpenstackConnectionFactory.getInstance().setCredential(username, password).authenticate(endpoint,
						tenant);

				log.debug("Reading heat template definition for openstack");

				URL url = this.getClass().getResource("/openstack/template.yml");

				try {
					heatTemplate = this.readTemplateFile(url);
				} catch (IOException | URISyntaxException e) {
					log.info("Failed to load heat template", e);
				}

				Assert.notNull(url, "Heat template definition must be provided.");

				endpointAvailabilityService.add(OPENSTACK_SERVICE_KEY,
						new EndpointServiceState(OPENSTACK_SERVICE_KEY, AvailabilityState.AVAILABLE));
			}
		} catch (Exception ex) {
			endpointAvailabilityService.add(OPENSTACK_SERVICE_KEY,
					new EndpointServiceState(OPENSTACK_SERVICE_KEY, AvailabilityState.ERROR, ex.toString()));
		}
	}

	private String readTemplateFile(URL url) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(url.toURI()));
		return new String(encoded, DEFAULT_ENCODING);
	}

	protected Stack create(String instanceId, Map<String, String> customParameters) throws PlatformException {
		Map<String, String> completeParameters = new HashMap<String, String>();
		completeParameters.putAll(defaultParameters());
		completeParameters.putAll(customParameters);

		String name = uniqueName(instanceId);

		Stack stack = heatFluent.create(name, heatTemplate, completeParameters, DEFAULT_DISABLE_ROLLBACK,
				DEFAULT_TIMEOUT_MINUTES);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			throw new PlatformException(e);
		}

		stack = heatFluent.get(name);

		if (stack.getStatus().equals(CREATE_FAILED))
			throw new PlatformException(stack.getStackStatusReason());

		while (stack.getStatus().equals(CREATE_IN_PROGRESS)) {
			stack = heatFluent.get(name);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new PlatformException(e);
			}
		}

		return stack;
	}

	protected void delete(String internalId) {
		Stack stack = heatFluent.get(internalId);

		heatFluent.delete(stack.getName(), stack.getId());
	}

	public static String uniqueName(String instanceId) {
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
