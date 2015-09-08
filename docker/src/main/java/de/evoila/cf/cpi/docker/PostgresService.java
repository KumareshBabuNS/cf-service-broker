/**
 * 
 */
package de.evoila.cf.cpi.docker;

import org.springframework.stereotype.Service;

/**
 * 
 * @author Dennis Müller.
 *
 */
public class PostgresService extends DockerServiceFactory {	

	@Override
	protected String getType() {
		return "postgres";
	}

	@Override
	protected int getOffset() {
		return 37;
	}

	@Override
	protected String getImageName() {
		return "postgresql";
	}

	@Override
	protected int getSevicePort() {
		return 5432;
	}

	@Override
	protected String getContainerEnviornment() {
		return "POSTGRES_PASSWORD=123456";
	}

	@Override
	protected String getContainerVolume() {
		return "/var/lib/postgresql/data";
	}

	@Override
	protected String getPassword() {
		return "123456";
	}

	@Override
	protected String getUsername() {
		return "postgres";
	}

	@Override
	protected String getVhost() {
		return null;
	}
	
	

}
