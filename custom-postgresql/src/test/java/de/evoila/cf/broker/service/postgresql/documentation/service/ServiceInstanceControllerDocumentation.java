/**
 * 
 */
package de.evoila.cf.broker.service.postgresql.documentation.service;

import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.data.ServiceInstanceRequestData;
import de.evoila.cf.broker.service.MockMvcTest;
import de.evoila.cf.broker.service.ObjectMappingConverter;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class ServiceInstanceControllerDocumentation extends MockMvcTest {
	
	private ServiceInstanceRequest serviceInstanceRequest;
	
	@Autowired
	private ServiceDefinition serviceDefinition;

	@Test
	public void testInstanceCreationSync() throws IOException, Exception {
		serviceInstanceRequest = ServiceInstanceRequestData.createServiceInstanceRequest(
				serviceDefinition.getId(), this.getPlanByPlatfromType(serviceDefinition, 
						"openstack", Platform.OPENSTACK).getId());
		
		mockMvc
			.perform(put("/service_instances/" + UUID.randomUUID().toString())
					.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
					.contentType(MediaType.APPLICATION_JSON)
					.content(ObjectMappingConverter.convertObjectToJsonBytes(serviceInstanceRequest))
			).andExpect(status().isAccepted())
			.andDo(document("create-service-instance-example"));
	}
	
	@Test
	public void testInstanceCreationAsync() throws IOException, Exception {
		serviceInstanceRequest = ServiceInstanceRequestData.createServiceInstanceRequest(
				serviceDefinition.getId(), this.getPlanByPlatfromType(serviceDefinition, 
						"openstack", Platform.OPENSTACK).getId());
		
		mockMvc
			.perform(put("/service_instances/" + UUID.randomUUID().toString())
					.param("accepts_incomplete", "true")
					.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
					.contentType(MediaType.APPLICATION_JSON)
					.content(ObjectMappingConverter.convertObjectToJsonBytes(serviceInstanceRequest))
			).andExpect(status().isAccepted())
			.andDo(document("create-service-instance-example"));
	}
	
}
