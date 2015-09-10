/**
 * 
 */
package de.evoila.cf.broker.service.postgres;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.evoila.cf.broker.service.postgres.jdbc.JdbcService;

/**
 * @author Johannes Hiemer
 *
 */
@Component
public class PostgresCustomImplementation {
	
	@Autowired
	private JdbcService jdbcService;

	public void createRoleForInstance(String instanceId) throws SQLException {
        jdbcService.checkValidUUID(instanceId);
        jdbcService.executeUpdate("CREATE ROLE \"" + instanceId + "\"");
        jdbcService.executeUpdate("ALTER DATABASE \"" + instanceId + "\" OWNER TO \"" + instanceId + "\"");
    }

    public void deleteRole(String instanceId) throws SQLException {
        jdbcService.checkValidUUID(instanceId);
        jdbcService.executeUpdate("DROP ROLE IF EXISTS \"" + instanceId + "\"");
    }

    public String bindRoleToDatabase(String dbInstanceId) throws SQLException {
        jdbcService.checkValidUUID(dbInstanceId);

        SecureRandom random = new SecureRandom();
        String passwd = new BigInteger(130, random).toString(32);
        
        jdbcService.executeUpdate("CREATE ROLE \"" + dbInstanceId + "\"");
        jdbcService.executeUpdate("ALTER ROLE \"" + dbInstanceId + "\" LOGIN password '" + passwd + "'");
        return passwd;
    }

    public void unbindRoleFromDatabase(String dbInstanceId) throws SQLException{
        jdbcService.checkValidUUID(dbInstanceId);
        jdbcService.executeUpdate("ALTER ROLE \"" + dbInstanceId + "\" NOLOGIN");
    }
}
