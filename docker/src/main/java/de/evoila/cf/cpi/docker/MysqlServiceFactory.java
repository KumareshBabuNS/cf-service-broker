package de.evoila.cf.cpi.docker;

import org.springframework.stereotype.Controller;


@Controller
public class MysqlServiceFactory extends IDockerServiceFactory {
	
	@Override
	protected String getType() {
		return "mysql";
	}

	@Override
	protected int getOffset() {
		return 118;
	}

	@Override
	protected String getImageName() {
		return "mysql";
	}

	@Override
	protected int getSevicePort() {
		return 3306;
	}

	@Override
	protected String getContainerEnviornment() {
		return "MYSQL_ROOT_PASSWORD=123456";
	}

	@Override
	protected String getContainerVolume() {
		return "/var/lib/mysql";
	}

	@Override
	protected String getPassword() {
		return "123456";
	}

	@Override
	protected String getUsername() {
		return "root";
	}

	@Override
	protected String getVhost() {
		return null;
	}

}
