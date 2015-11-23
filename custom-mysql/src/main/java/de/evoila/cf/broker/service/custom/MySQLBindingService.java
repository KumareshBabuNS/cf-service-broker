/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.service.mysql.MySQLCustomImplementation;
import de.evoila.cf.broker.service.mysql.jdbc.MySQLDbService;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class MySQLBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MySQLDbService jdbcService;

	@Autowired
	private MySQLCustomImplementation mysqlCustomImplementation;

	private boolean connection(ServiceInstance serviceInstance) throws SQLException {
		if (jdbcService.isConnected())
			return true;
		else {
			Assert.notNull(serviceInstance, "ServiceInstance may not be null");
			Assert.notNull(serviceInstance.getId(), "Id of ServiceInstance may not be null");
			Assert.notNull(serviceInstance.getHost(), "Host of ServiceInstance may not be null");
			Assert.notNull(serviceInstance.getPort(), "Port of ServiceInstance may not be null");
			
			return jdbcService.createConnection(serviceInstance.getId(), serviceInstance.getHost(),
					serviceInstance.getPort());
		}
	}

	public void create(ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		try {
			connection(serviceInstance);
		} catch (SQLException e1) {
			throw new ServiceBrokerException("Could not connect to database");
		}

		String instanceId = serviceInstance.getId();

		try {
			jdbcService.executeUpdate("CREATE DATABASE \"" + instanceId + "\" ENCODING 'UTF8'");
			jdbcService.executeUpdate("REVOKE all on database \"" + instanceId + "\" from public");
		} catch (SQLException e) {
			log.error(e.toString());
			throw new ServiceBrokerException("Could not add to database");
		}
	}

	public void delete(ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		try {
			connection(serviceInstance);
		} catch (SQLException e1) {
			throw new ServiceBrokerException("Could not connect to database");
		}

		String instanceId = serviceInstance.getId();

		try {
			jdbcService.executeUpdate("REVOKE all on database \"" + instanceId + "\" from public");
			jdbcService.executeUpdate("DROP DATABASE \"" + instanceId + "\"");
		} catch (SQLException e) {
			log.error(e.toString());
			throw new ServiceBrokerException("Could not remove from database");
		}
	}
	
	private String username(String bindingId) {
		return bindingId.replace("-", "").substring(0, 10);
	}

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		try {
			connection(serviceInstance);
		} catch (SQLException e1) {
			throw new ServiceBrokerException("Could not connect to database");
		}
		
		String username = username(bindingId);
		String password = "";
		try {
			password = mysqlCustomImplementation.bindRoleToDatabase(serviceInstance.getId(), username);
		} catch (SQLException e) {
			log.error(e.toString());
			throw new ServiceBrokerException("Could not update database");
		}

		String dbURL = String.format("mysql://%s:%s@%s:%d/%s", username, password,
				jdbcService.getHost(), jdbcService.getPort(), serviceInstance.getId());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		try {
			connection(serviceInstance);
		} catch (SQLException e1) {
			throw new ServiceBrokerException("Could not connect to database");
		}

		try {
			String username = username(bindingId);
			mysqlCustomImplementation.unbindRoleFromDatabase(username);
		} catch (SQLException e) {
			log.error(e.toString());
			throw new ServiceBrokerException("Could not remove from database");
		}
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

}
