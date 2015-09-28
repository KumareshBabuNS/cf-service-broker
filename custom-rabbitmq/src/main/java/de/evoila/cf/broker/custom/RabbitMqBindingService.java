/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.evoila.cf.broker.custom.mongodb.RabbitMqService;
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

	private static final String DEFAULT_VHOST = "evoila";

	private static final String API = "/api/";

	private static final String IP_PORT_SEPARATOR = ":";

	private static final String URL_PATTERN = AMQP + STRING_URL_VALUE + USER_PASSWORD_SEPARATOR + STRING_URL_VALUE
			+ CREDENTIAL_IP_SEPARATOR + STRING_URL_VALUE + IP_PORT_SEPARATOR + DOUBLE_URL_VALUE + PATH_SEPARATOR
			+ STRING_URL_VALUE;

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitMqService rabbitMqService;

	@Value("${rabbit.admin:evoila}")
	private String adminUser;

	@Value("${rabbit.admin.passw:evoila}")
	private String adminPassword;

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
		String vhostName = DEFAULT_VHOST;

		// Create user and password
		String userName = bindingId;

		SecureRandom random = new SecureRandom();
		String password = new BigInteger(130, random).toString(32);

		addUserToVHostAndSetPermissions(userName, amqpHostAddress, password, vhostName);

		String dbURL = String.format(URL_PATTERN, userName, password, amqpHostAddress, serviceInstance.getPort(),
				vhostName);

		try {
			connection(serviceInstance, vhostName, userName, password);
		} catch (IOException e) {
			throw new ServiceBrokerException("Could not open RabbitMQ connection", e);
		}

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	private void addUserToVHostAndSetPermissions(String userName, String amqpHostAddress, String password,
			String vhostName) {
		RestTemplate restTemplate = new RestTemplate();
		String amqpApi = getAmqpApi(amqpHostAddress);
		restTemplate.put(amqpApi + "/users/" + userName, "{\"password\":\"" + password + "\"}");
		restTemplate.put(amqpApi + "/permissions/" + vhostName + PATH_SEPARATOR + userName,
				"{\"configure\":\"^$\",\"write\":\".*\",\"read\":\".*\"}");
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		// Delete User from vHost
		deleteUserFromVhost(bindingId, serviceInstance);
	}

	private void deleteUserFromVhost(String bindingId, ServiceInstance serviceInstance) {
		String amqpHostAddress = serviceInstance.getHost();
		String amqpApi = getAmqpApi(amqpHostAddress);

		RestTemplate restTemplate = new RestTemplate();
		String userName = bindingId;
		restTemplate.delete(amqpApi + "/users/" + userName, "");
	}

	private String getAmqpApi(String amqpHostAddress) {
		String amqpApi = HTTP + adminUser + USER_PASSWORD_SEPARATOR + adminPassword + CREDENTIAL_IP_SEPARATOR
				+ amqpHostAddress + API;
		return amqpApi;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new NotImplementedException();
	}

}
