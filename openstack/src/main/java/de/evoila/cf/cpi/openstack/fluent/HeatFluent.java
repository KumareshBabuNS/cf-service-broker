/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.heat.StackService;

import de.evoila.cf.cpi.openstack.fluent.nova.NovaFluentConnectionFactory;

/**
 * @author Johannes Hiemer.
 *
 */
public class HeatFluent {
	
	private OSClient client() {
		return NovaFluentConnectionFactory.connection();
	}
	
	public StackService list() {
		return client().heat().stacks();
	}
	
	public void create() {
	}
}
