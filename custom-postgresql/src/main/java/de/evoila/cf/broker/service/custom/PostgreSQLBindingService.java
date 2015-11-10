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

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.service.postgres.PostgresCustomImplementation;
import de.evoila.cf.broker.service.postgres.jdbc.PostgresDbService;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class PostgreSQLBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PostgresDbService jdbcService;

	@Autowired
	private PostgresCustomImplementation postgresCustomImplementation;

	private boolean connection(ServiceInstance serviceInstance) throws SQLException {
		if (jdbcService.isConnected())
			return true;
		else {
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

	@Override
	protected ServiceInstanceBindingResponse bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		try {
			connection(serviceInstance);
		} catch (SQLException e1) {
			throw new ServiceBrokerException("Could not connect to database");
		}

		String password = "";
		try {
			password = postgresCustomImplementation.bindRoleToDatabase(serviceInstance.getId(), bindingId);
		} catch (SQLException e) {
			log.error(e.toString());
			throw new ServiceBrokerException("Could not update database");
		}

		String dbURL = String.format("postgres://%s:%s@%s:%d/%s", bindingId, password,
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
			postgresCustomImplementation.unbindRoleFromDatabase(bindingId);
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
