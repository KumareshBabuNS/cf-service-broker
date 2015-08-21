/**
 * 
 */
package de.evoila.cf.cpi.openstack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.nova.Nova;

import de.evoila.cf.cpi.credential.CredentialFactory;

/**
 * 
 * @author Dennis Mueller, evoila.
 *
 */
public class NovaFactory extends CredentialFactory {
	
	private static final Logger log = LoggerFactory
			.getLogger(NovaFactory.class);
	
	protected Nova novaClient; 

	public static final String KEYSTONE_AUTH_URL = "https://identity.api.rackspacecloud.com/v2.0";
	
	public static final String NOVA_ENDPOINT = "https://lon.servers.api.rackspacecloud.com/v2";
	
	private String username;

	private String password;
	
	public void setCredential(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void initialize() {
		String provider = "openstack-nova";
		initialize(provider);
	}

	public void initialize(String provider) {
		log.info("Initialize Openstack");
		
		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate()
				.withUsernamePassword(username, password)
				.execute();

		keystone.token(access.getToken().getId());
		
		novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(access.getToken().getTenant().getId()));
		novaClient.token(access.getToken().getId());	
	}

}
