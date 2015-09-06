/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.evoila.cf.cpi.BaseIntegrationTest;

/**
 * @author Johannes Hiemer.
 *
 */
public class PostgresServiceTest extends BaseIntegrationTest {
	
	@Autowired
	private PostgresService postgresService;
	
	@Test
	public void createTest() throws InterruptedException {
		Map<String, String> customParameters = new HashMap<String, String>();
		customParameters.put("flavor", "3");
		customParameters.put("volume_size", "20");
		customParameters.put("database_name", "klsdfjklfdj");
		customParameters.put("database_user", "fiojfiosjfd");
		customParameters.put("database_password", "frjwfeoiwefjw");
		
		postgresService.create(customParameters);
	}

}
