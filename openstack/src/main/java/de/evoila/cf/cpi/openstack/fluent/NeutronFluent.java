/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.network.Network;

import de.evoila.cf.cpi.openstack.fluent.nova.NovaFluentConnectionFactory;


/**
 * @author Johannes Hiemer.
 *
 */
public class NeutronFluent {
	
	private OSClient client() {
		return NovaFluentConnectionFactory.connection();
	}
	
	public List<? extends Network> networks() {
		return client().networking().network().list();
	}
	
}
