/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.service.postgres.PostgresSqlImplementation;
import de.evoila.cf.broker.service.postgres.PostgresSqlRoleImplementation;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class PostgresServiceImplementation {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PostgresSqlImplementation postgresSqlImplementation;

	@Autowired
	private PostgresSqlRoleImplementation postgresSqlRoleImplementation;

	public void create(String instanceId, Plan plan) throws SQLException {
		postgresSqlImplementation.executeUpdate("CREATE DATABASE \"" + instanceId + "\" ENCODING 'UTF8'");
		postgresSqlImplementation.executeUpdate("REVOKE all on database \"" + instanceId + "\" from public");

	}

	public ServiceInstanceBinding bind(String bindingId, String appGuid, ServiceInstance serviceInstance) {
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

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, null, appGuid);
	}

	public ServiceInstanceBinding unbind(String bindingId, ServiceInstance serviceInstance) {
		try {
			postgresSqlRoleImplementation.unBindRoleFromDatabase(serviceInstance.getId());
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), null, null, null);
	}

	public void delete(String instanceId) throws SQLException {
		postgresSqlImplementation.checkValidUUID(instanceId);

		Map<Integer, String> parameterMap = new HashMap<Integer, String>();
		parameterMap.put(1, instanceId);

		try {
			Map<String, String> result = postgresSqlImplementation.executeSelect("SELECT current_user");
			String currentUser = null;

			if (result != null) {
				currentUser = result.get("current_user");
			}

			if (currentUser == null) {
				log.error("Current user could not be found?");
			}

			postgresSqlImplementation.executePreparedSelect(
					"SELECT pg_terminate_backend(pg_stat_activity.pid) FROM "
							+ "pg_stat_activity WHERE pg_stat_activity.datname = ? AND pid <> pg_backend_pid()",
					parameterMap);

			postgresSqlImplementation
					.executeUpdate("ALTER DATABASE \"" + instanceId + "\" OWNER TO \"" + currentUser + "\"");

			postgresSqlImplementation.executeUpdate("DROP DATABASE IF EXISTS \"" + instanceId + "\"");

			postgresSqlImplementation.executePreparedUpdate("DELETE FROM service WHERE serviceinstanceid=?",
					parameterMap);
		} catch (SQLException e) {
			log.error(e.getMessage());
		}

	}

}
