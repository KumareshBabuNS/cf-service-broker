/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.BaseIntegrationTest;

/**
 * @author Johannes Hiemer.
 *
 */
public class OpenstackPlatformServiceTest extends BaseIntegrationTest {
	
	private Plan plan;
	
	private ServiceInstance serviceInstance;
	
	@Autowired
	private OpenstackPlatformService openstackPlatformTest;
	
	@Before
	public void before() {
		plan = new Plan("basic", "500 MB PostgreSQL DB Basic Instance",
				"The most basic PostgreSQL plan currently available. Providing"
			+ "500 MB of capcity in a PostgreSQL DB.", Platform.OPENSTACK, 25, "3", 10);
		
		serviceInstance = new ServiceInstance(UUID.randomUUID().toString(), 
				UUID.randomUUID().toString(), plan.getId(), 
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), 
				null, "http://currently.not/available");
	}
	
	@Test
	public void isSyncPossibleOnCreateTest() {
		boolean result = openstackPlatformTest.isSyncPossibleOnCreate(plan);
		
		Assert.assertTrue(result == false);
	}
	
	@Test
	public void isSyncPossibleOnDeleteTest() {
		boolean result = openstackPlatformTest.isSyncPossibleOnDelete(serviceInstance);
		
		Assert.assertTrue(result == false);
	}
	
	@Test
	public void isSyncPossibleOnUpdateTest() {
		boolean result = openstackPlatformTest.isSyncPossibleOnUpdate(serviceInstance, plan);
		
		Assert.assertTrue(result == false);
	}
	
	@Test
	public void createInstanceTest() throws InterruptedException {
		openstackPlatformTest.createInstance(serviceInstance, plan);
	}
	
	@Test
	public void deleteInstanceTest() throws ServiceBrokerException, 
		ServiceInstanceDoesNotExistException {
		openstackPlatformTest.createInstance(serviceInstance, plan);
		
		openstackPlatformTest.deleteServiceInstance(serviceInstance);
	}
	
	@Test(expected=NotImplementedException.class)
	public void updateInstanceTest() {
		openstackPlatformTest.updateInstance(serviceInstance, plan);
	}


}