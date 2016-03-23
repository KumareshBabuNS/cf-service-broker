/**
 * 
 */
package de.evoila.cf.broker.service.mysql;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.mysql.jdbc.MySQLDbService;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class MySQLCustomImplementation {

	@Autowired
	private MySQLDbService jdbcService;

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
		SecureRandom random = new SecureRandom();
		String passwd = new BigInteger(130, random).toString(32);

		jdbcService.executeUpdate("CREATE USER \"" + bindingId + "\" IDENTIFIED BY \"" + passwd + "\"");
		jdbcService.executeUpdate("GRANT ALL PRIVILEGES ON `" + serviceInstanceId + "`.* TO `" + bindingId + "`@\"%\"");
		jdbcService.executeUpdate("FLUSH PRIVILEGES");

		return passwd;
	}

	public void unbindRoleFromDatabase(String bindingId) throws SQLException {
		jdbcService.executeUpdate("DROP USER \"" + bindingId + "\"");
	}

}
