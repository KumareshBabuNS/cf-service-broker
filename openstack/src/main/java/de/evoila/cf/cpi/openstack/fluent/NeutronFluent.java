/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import java.util.List;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.network.Network;

import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;


/**
 * @author Johannes Hiemer.
 *
 */
public class NeutronFluent {
	
	private OSClient client() {
		return OpenstackConnectionFactory.connection();
	}
	
	public List<? extends Network> networks() {
		return client().networking().network().list();
	}
	
}
