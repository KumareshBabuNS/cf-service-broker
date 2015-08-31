/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;

import de.evoila.cf.cpi.openstack.BaseConnectionFactoryTest;

/**
 * @author Johannes Hiemer.
 *
 */
public class NovaFluentServerTest extends BaseConnectionFactoryTest {
	
	NovaFluent novaFluent = null;
	
	@Before
	public void before() {
		novaFluent = new NovaFluent();
	}
	
	@Test
	public void testFlavorList() {
		List<? extends Flavor> flavors = novaFluent.getFlavors();
		
		Assert.assertNotNull(flavors);
		Assert.assertTrue(flavors.size() > 0);
	}

	@Test
	public void testCreateAndDeleteServer() {
		Server server = novaFluent.createInstace("test123", 
				"cf-microbosh",
				"af9e47bc-e3fc-4844-8a0f-c09bf063d66d",
				"3", 
				Arrays.asList("acdece87-6afc-43c6-9e73-bed2b3bc0ea3"),
				true);
		
		Assert.assertNotNull(server);
		novaFluent.deleteInstance(server.getId());
	}

}
