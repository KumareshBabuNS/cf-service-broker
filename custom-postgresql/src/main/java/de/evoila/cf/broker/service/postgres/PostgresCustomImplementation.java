/**
 * 
 */
package de.evoila.cf.broker.service.postgres;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.postgres.jdbc.PostgresDbService;

/**
 * @author Johannes Hiemer
 *
 */
@Service
public class PostgresCustomImplementation {

	@Autowired
	private PostgresDbService jdbcService;

	public void initServiceInstance(ServiceInstance serviceInstance, String[] databases) throws SQLException {
		String serviceInstanceId = serviceInstance.getId();
		if (!jdbcService.isConnected()) {
			jdbcService.createConnection(serviceInstanceId, serviceInstance.getHost(), serviceInstance.getPort());
		}
		// jdbcService.checkValidUUID(instanceId);
		jdbcService.executeUpdate("CREATE ROLE \"" + serviceInstanceId + "\"");
		for (String database : databases) {
			jdbcService.executeUpdate("CREATE DATABASE \"" + database + "\" OWNER \"" + serviceInstanceId + "\"");
		}
		// for (String database : databases) {
		// jdbcService.executeUpdate("ALTER DATABASE \"" + database + "\" OWNER
		// TO \"" + instanceId + "\"");
		// }
	}

	public void deleteRole(String instanceId) throws SQLException {
		jdbcService.checkValidUUID(instanceId);
		jdbcService.executeUpdate("DROP ROLE IF EXISTS \"" + instanceId + "\"");
	}

	public String bindRoleToDatabase(String serviceInstanceId, String bindingId) throws SQLException {
		jdbcService.checkValidUUID(bindingId);

		SecureRandom random = new SecureRandom();
		String passwd = new BigInteger(130, random).toString(32);

		jdbcService.executeUpdate("CREATE ROLE \"" + bindingId + "\"");
		jdbcService.executeUpdate("ALTER ROLE \"" + bindingId + "\" LOGIN password '" + passwd + "'");
		jdbcService.executeUpdate(
				// "GRANT ALL PRIVILEGES ON DATABASE \"" + serviceInstanceId +
				// "\" TO \"" + bindingId + "\"");
				"GRANT \"" + serviceInstanceId + "\" TO \"" + bindingId + "\"");
		return passwd;
	}

	public void unbindRoleFromDatabase(String bindingId) throws SQLException {
		jdbcService.checkValidUUID(bindingId);
		jdbcService.executeUpdate("ALTER ROLE \"" + bindingId + "\" NOLOGIN");
	}
}
