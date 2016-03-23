/**
 * 
 */
package de.evoila.cf.broker.service.postgres;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.postgres.jdbc.PostgresDbService;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class PostgresCustomImplementation {

	@Autowired
	private PostgresDbService jdbcService;

	public void initServiceInstance(ServiceInstance serviceInstance, String[] databases) throws SQLException {
		String serviceInstanceId = serviceInstance.getId();
		if (!jdbcService.isConnected()) {
			ServerAddress host = serviceInstance.getHosts().get(0);
			jdbcService.createConnection(serviceInstanceId, host.getIp(), host.getPort());
		}
		jdbcService.executeUpdate("CREATE ROLE \"" + serviceInstanceId + "\"");
		for (String database : databases) {
			jdbcService.executeUpdate("CREATE DATABASE \"" + database + "\" OWNER \"" + serviceInstanceId + "\"");
		}
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
		jdbcService.executeUpdate("GRANT \"" + serviceInstanceId + "\" TO \"" + bindingId + "\"");

		jdbcService.executeUpdate("GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"" + bindingId + "\"");
		jdbcService.executeUpdate("ALTER DEFAULT PRIVILEGES FOR ROLE \"" + bindingId
				+ "\" IN SCHEMA public GRANT ALL ON TABLES TO \"" + bindingId + "\"");
		jdbcService.executeUpdate("GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO \"" + bindingId + "\"");
		jdbcService.executeUpdate("ALTER DEFAULT PRIVILEGES FOR ROLE \"" + bindingId
				+ "\" IN SCHEMA public GRANT ALL ON SEQUENCES TO \"" + bindingId + "\"");

		return passwd;
	}

	public void unbindRoleFromDatabase(String bindingId) throws SQLException {
		jdbcService.checkValidUUID(bindingId);
		jdbcService.executeUpdate("ALTER ROLE \"" + bindingId + "\" NOLOGIN");
	}
}
