/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openstack4j.api.exceptions.ClientResponseException;

import de.evoila.cf.cpi.openstack.fluent.nova.NovaFluentConnectionFactory;

/**
 * @author Johannes Hiemer.
 *
 */
public class NovaFluentConnectionFactoryTest {
	
	private String username = "jhiemer";
	
	private String password = "Vwg7pCRaWuiAeq7bsk74";
	
	private String wrongPassword = "12345678";
	
	private String authUrlV2 = "http://85.10.239.86:5000/v2.0";
	
	private String authUrlV3 = "http://85.10.239.86:5000/v3.0";
	
	private String tenant = "Cloudfoundry";

	@Test
	public void authenticationV2ShouldWork() {
		NovaFluentConnectionFactory.getInstance()
			.setCredential(username, password)
			.authenticate(authUrlV2, tenant);
		
		assertNotNull(NovaFluentConnectionFactory.connection());
		assertNotNull(NovaFluentConnectionFactory.connection().getToken());
		
	}
	
	@Test(expected = Exception.class)
	public void authenticationV2Fail() {
		NovaFluentConnectionFactory.getInstance()
		.setCredential(username, wrongPassword)
		.authenticate(authUrlV2, tenant);
	}
	
	@Test(expected = ClientResponseException.class)
	public void authenticationV3ShouldWork() {
		NovaFluentConnectionFactory.getInstance()
		.setCredential(username, password)
		.authenticateV3(authUrlV3, tenant);
		
		assertNotNull(NovaFluentConnectionFactory.connection());
		assertNotNull(NovaFluentConnectionFactory.connection().getToken());
	}
	
	@Test(expected = Exception.class)
	public void authenticationV3Fail() {
		NovaFluentConnectionFactory.getInstance()
		.setCredential(username, wrongPassword)
		.authenticate(authUrlV2, tenant);
	}

}
