/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.evoila.cf.broker.custom.rabbitmq.RabbitMqService;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class RabbitMqBindingService extends BindingServiceImpl {

	private static final String DOUBLE_URL_VALUE = "%d";

	private static final String STRING_URL_VALUE = "%s";

	private static final String PATH_SEPARATOR = "/";

	private static final String AMQP = "amqp://";

	private static final String USER_PASSWORD_SEPARATOR = ":";

	private static final String CREDENTIAL_IP_SEPARATOR = "@";

	private static final String HTTP = "http://";

	private static final String API = "/api";

	private static final String IP_PORT_SEPARATOR = ":";
	
	private static final int API_PORT = 15672;

	private static final String URL_PATTERN = AMQP + STRING_URL_VALUE + USER_PASSWORD_SEPARATOR + STRING_URL_VALUE
			+ CREDENTIAL_IP_SEPARATOR + STRING_URL_VALUE + IP_PORT_SEPARATOR + DOUBLE_URL_VALUE + PATH_SEPARATOR
			+ STRING_URL_VALUE;
	
	private RestTemplate restTemplate = new RestTemplate();

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitMqService rabbitMqService;

	private boolean connection(ServiceInstance serviceInstance, String vhostName, String userName, String password)
			throws IOException {
		if (rabbitMqService.isConnected())
			return true;
		else {
			log.info("Opening connection to " + serviceInstance.getHost() + serviceInstance.getPort());
			rabbitMqService.createConnection(serviceInstance.getId(), serviceInstance.getHost(),
					serviceInstance.getPort(), vhostName, userName, password);
		}
		return true;
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {
		String amqpHostAddress = serviceInstance.getHost();
		String vhostName = serviceInstance.getId();

		String userName = bindingId;
		SecureRandom random = new SecureRandom();
		String password = new BigInteger(130, random).toString(32);

		addUserToVHostAndSetPermissions(serviceInstance.getId(), userName, amqpHostAddress, password, vhostName);

		String rabbitMqUrl = String.format(URL_PATTERN, userName, password, amqpHostAddress, serviceInstance.getPort(),
				vhostName);

		try {
			connection(serviceInstance, vhostName, userName, password);
		} catch (IOException e) {
			throw new ServiceBrokerException("Could not open RabbitMQ connection", e);
		}

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", rabbitMqUrl);

		return new ServiceInstanceBindingResponse(credentials);
	}

	private void addUserToVHostAndSetPermissions(String instanceId, String userName, String amqpHostAddress, String password,
			String vhostName) {
		
		executeRequest(getAmqpApi(amqpHostAddress, API_PORT) + "/users/" + userName, 
				HttpMethod.PUT, instanceId, "{\"password\":\"" + password + "\", \"tags\" : \"none\"}");
		
		executeRequest(getAmqpApi(amqpHostAddress, API_PORT) + "/permissions/" + vhostName + PATH_SEPARATOR + userName,
				HttpMethod.PUT, instanceId, "{\"configure\":\".*\",\"write\":\".*\",\"read\":\".*\"}");
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		deleteUserFromVhost(bindingId, serviceInstance);
	}

	private void deleteUserFromVhost(String bindingId, ServiceInstance serviceInstance) {

		executeRequest(getAmqpApi(serviceInstance.getHost(), API_PORT) + "/users/" + bindingId, 
				HttpMethod.DELETE, serviceInstance.getId(), null);
	}

	private String getAmqpApi(String amqpHostAddress, int port) {
		return HTTP + amqpHostAddress + ":" + port + API;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}
	
	private void executeRequest(String url, HttpMethod method, String instanceId, String payload) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", buildAuthHeader(instanceId, instanceId));
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		log.info("Requesting: " + url + " and method " + method.toString());
		
		HttpEntity<String> entity = null;
		if (payload == null)
			entity = new HttpEntity<String>(headers);
		else
			entity = new HttpEntity<String>(payload, headers);

		restTemplate.exchange(url, method, entity, String.class);
	}
	
	private String buildAuthHeader(String username, String password) {
		String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));

        return "Basic " + new String(encodedAuth);
	}

}
