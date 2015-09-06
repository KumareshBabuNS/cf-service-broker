/**
 * 
 */
package de.evoila.cf.broker.service.postgres;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Johannes Hiemer
 *
 */
@Component
public class PostgresSqlRoleImplementation {
	
	@Autowired
	private PostgresSqlImplementation postgresSqlImplementation;

	public void createRoleForInstance(String instanceId) throws SQLException {
        postgresSqlImplementation.checkValidUUID(instanceId);
        postgresSqlImplementation.executeUpdate("CREATE ROLE \"" + instanceId + "\"");
        postgresSqlImplementation.executeUpdate("ALTER DATABASE \"" + instanceId + "\" OWNER TO \"" + instanceId + "\"");
    }

    public void deleteRole(String instanceId) throws SQLException {
        postgresSqlImplementation.checkValidUUID(instanceId);
        postgresSqlImplementation.executeUpdate("DROP ROLE IF EXISTS \"" + instanceId + "\"");
    }

    public String bindRoleToDatabase(String dbInstanceId) throws SQLException {
        postgresSqlImplementation.checkValidUUID(dbInstanceId);

        SecureRandom random = new SecureRandom();
        String passwd = new BigInteger(130, random).toString(32);

        postgresSqlImplementation.executeUpdate("ALTER ROLE \"" + dbInstanceId + "\" LOGIN password '" + passwd + "'");
        return passwd;
    }

    public void unBindRoleFromDatabase(String dbInstanceId) throws SQLException{
        postgresSqlImplementation.checkValidUUID(dbInstanceId);
        postgresSqlImplementation.executeUpdate("ALTER ROLE \"" + dbInstanceId + "\" NOLOGIN");
    }
}
