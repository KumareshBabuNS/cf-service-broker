/**
 * 
 */
package de.evoila.cf.broker.service.postgres;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.evoila.cf.broker.service.postgres.jdbc.MongoDbService;

/**
 * @author Johannes Hiemer
 *
 */
@Component
public class PostgresCustomImplementation {
	
	@Autowired
	private MongoDbService jdbcService;

	public void createRoleForInstance(String instanceId) throws SQLException {
        jdbcService.checkValidUUID(instanceId);
        jdbcService.executeUpdate("CREATE ROLE \"" + instanceId + "\"");
        jdbcService.executeUpdate("ALTER DATABASE \"" + instanceId + "\" OWNER TO \"" + instanceId + "\"");
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
        jdbcService.executeUpdate("GRANT ALL PRIVILEGES ON DATABASE \"" + serviceInstanceId + "\" TO \"" + bindingId + "\"");
        return passwd;
    }

    public void unbindRoleFromDatabase(String dbInstanceId) throws SQLException{
        jdbcService.checkValidUUID(dbInstanceId);
        jdbcService.executeUpdate("ALTER ROLE \"" + dbInstanceId + "\" NOLOGIN");
    }
}
