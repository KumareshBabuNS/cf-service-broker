/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import de.evoila.cf.cpi.openstack.util.StackProgressObserver;

/**
 * @author Christian Mueller, evoila
 *
 */
@Service(value="defaultStackHandler")
public class StackHandler {
	/**
	 * 
	 */
	private static final String TEMPLATE = "template";

	public static final boolean DEFAULT_DISABLE_ROLLBACK = false;

	public static final long DEFAULT_TIMEOUT_MINUTES = 10;
	
	private static String DEFAULT_ENCODING = "UTF-8";
	
	public static final String IMAGE_ID = "image_id";
	public static final String KEYPAIR = "keypair";
	public static final String NETWORK_ID = "network_id";
	public static final String AVAILABILITY_ZONE = "availability_zone";
	public static final String LOG_PORT = "log_port";
	public static final String LOG_HOST = "log_host";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private String defaultHeatTemplate;
		
	@Value("${openstack.networkId}")
	private String networkId;

	@Value("${openstack.imageId}")
	private String imageId;

	@Value("${openstack.keypair}")
	private String keypair;

	@Value("${openstack.cinder.az}")
	private String availabilityZone;
	
	@Autowired
	protected HeatFluent heatFluent;
	
	@Autowired
	protected StackProgressObserver stackProgressObserver;

	@PostConstruct
	public void initialize() {
		final String templatePath = "/openstack/template.yml";
		defaultHeatTemplate = accessTemplate(templatePath);
	}
	

	protected String accessTemplate(final String templatePath) {
		URL url = this.getClass().getResource(templatePath);

		Assert.notNull(url, "Heat template definition must be provided.");
		try {
			return this.readTemplateFile(url);
		} catch (IOException | URISyntaxException e) {
			log.info("Failed to load heat template", e);
			return defaultHeatTemplate;
		}
	}

	private String readTemplateFile(URL url) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(url.toURI()));
		return new String(encoded, DEFAULT_ENCODING);
	}
	
	public void create(String instanceId, Map<String, String> customParameters)
			throws PlatformException, InterruptedException {
		
		
		String heatTemplate;
		if (customParameters.containsKey(TEMPLATE)) {
			heatTemplate = accessTemplate(customParameters.get(TEMPLATE));
		} else {
			heatTemplate = getDefaultHeatTemplate();
		}

		Map<String, String> completeParameters = new HashMap<String, String>();
		completeParameters.putAll(defaultParameters());
		completeParameters.putAll(customParameters);

		String name = HeatFluent.uniqueName(instanceId);

		heatFluent.create(name, heatTemplate, completeParameters, DEFAULT_DISABLE_ROLLBACK,
				DEFAULT_TIMEOUT_MINUTES);

	}
	
	
	public void delete(String internalId) {
		Stack stack = heatFluent.get(internalId);

		heatFluent.delete(stack.getName(), stack.getId());
	}
	
	
	protected Map<String, String> defaultParameters() {
		Map<String, String> defaultParameters = new HashMap<String, String>();
		defaultParameters.put(IMAGE_ID, imageId);
		defaultParameters.put(KEYPAIR, keypair);
		defaultParameters.put(NETWORK_ID, networkId);
		defaultParameters.put(AVAILABILITY_ZONE, availabilityZone);

		return defaultParameters;
	}
	
	public String getDefaultHeatTemplate() {
		return defaultHeatTemplate;
	}
}
