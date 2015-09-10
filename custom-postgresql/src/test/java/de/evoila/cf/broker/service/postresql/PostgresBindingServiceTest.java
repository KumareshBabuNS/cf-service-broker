/**
 * 
 */
package de.evoila.cf.broker.service.postresql;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.evoila.cf.broker.exception.ServerviceInstanceBindingDoesNotExistsException;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.exception.ServiceInstanceBindingExistsException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.data.ServiceInstanceData;
import de.evoila.cf.broker.service.MockMvcTest;
import de.evoila.cf.broker.service.custom.PostgresBindingService;

/**
 * @author Johannes Hiemer.
 *
 */
public class PostgresBindingServiceTest extends MockMvcTest {
	
	private static Plan plan;
	
	private static ServiceInstance serviceInstance;
	
	@Autowired
	private PostgresBindingService postgresBindingService;
	
	@BeforeClass
	public static void beforeClass() {
		serviceInstance = ServiceInstanceData.createOpenstackData();
		serviceInstance.setHost("172.16.248.144");
		serviceInstance.setPort(5432);
		plan = ServiceInstanceData.createOpenstackPlanData();
	}
	
	@Test
	public void testCreation() throws SQLException {
		postgresBindingService.create(serviceInstance, plan);
		postgresBindingService.delete(serviceInstance, plan);
	}
	
	@Test
	public void testBinding() throws SQLException, ServiceInstanceBindingExistsException, ServiceBrokerException, 
		ServiceInstanceDoesNotExistException, ServiceDefinitionDoesNotExistException, ServerviceInstanceBindingDoesNotExistsException {
		postgresBindingService.create(serviceInstance, plan);
		String bindingId = UUID.randomUUID().toString();
		String appGuid = UUID.randomUUID().toString();
		
		postgresBindingService.createServiceInstanceBinding(bindingId, serviceInstance.getId(),
				serviceInstance.getServiceDefinitionId(), serviceInstance.getPlanId(), appGuid);
		postgresBindingService.deleteServiceInstanceBinding(bindingId);
		
		postgresBindingService.delete(serviceInstance, plan);
		
	}
	
	

}
