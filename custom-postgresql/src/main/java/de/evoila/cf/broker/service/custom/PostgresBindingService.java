/**
 * 
 */
package de.evoila.cf.broker.service.custom;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
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
import de.evoila.cf.broker.service.postgres.PostgresSqlImplementation;
import de.evoila.cf.broker.service.postgres.PostgresSqlRoleImplementation;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class PostgresBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PostgresSqlImplementation postgresSqlImplementation;

	@Autowired
	private PostgresSqlRoleImplementation postgresSqlRoleImplementation;

	public void create(String instanceId, Plan plan) throws SQLException {
		postgresSqlImplementation.executeUpdate("CREATE DATABASE \"" + instanceId + "\" ENCODING 'UTF8'");
		postgresSqlImplementation.executeUpdate("REVOKE all on database \"" + instanceId + "\" from public");

	}

	@Override
	protected ServiceInstanceBindingResponse bindService(
			ServiceInstance serviceInstance, Plan plan) throws ServiceBrokerException {
		String passwd = "";

		try {
			passwd = postgresSqlRoleImplementation.bindRoleToDatabase(serviceInstance.getId());
		} catch (SQLException e) {
			log.error(e.toString());
		}

		String dbURL = String.format("postgres://%s:%s@%s:%d/%s", serviceInstance.getId(), passwd,
				postgresSqlImplementation.getHost(), postgresSqlImplementation.getPort(), serviceInstance.getId());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	@Override
	protected void deleteBinding(ServiceInstance serviceInstance)
			throws ServiceBrokerException {
		try {
			postgresSqlRoleImplementation.unBindRoleFromDatabase(serviceInstance.getId());
		} catch (SQLException e) {
			log.error(e.toString());
		}
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new NotImplementedException();
	}

}
