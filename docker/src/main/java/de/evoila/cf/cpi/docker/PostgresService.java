/**
 * 
 */
package de.evoila.cf.cpi.docker;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Dennis Müller.
 *
 */
@Service("service")
public class PostgresService extends DockerServiceFactory {
	
	@PostConstruct
	public void init() {
		LoggerFactory.getLogger(getClass()).info("lhflashdf");
	}

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
