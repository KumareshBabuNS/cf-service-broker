/**
 * 
 */
package de.evoila.cf.broker.service.controller;

import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.ServiceInstanceRequest;
import de.evoila.cf.broker.model.data.InstanceBindingRequest;
import de.evoila.cf.broker.model.data.ServiceInstanceRequestData;
import de.evoila.cf.broker.service.MockMvcTest;
import de.evoila.cf.broker.service.ObjectMappingConverter;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class ServiceInstanceLifecycleTest extends MockMvcTest {

	private ServiceInstanceRequest serviceInstanceRequest;

	private ServiceInstanceBindingRequest bindingRequest;

	@Autowired
	private ServiceDefinition serviceDefinition;

	@Test
	public void testInstanceCreationSync() throws IOException, Exception {
		Plan plan = this.getPlanByPlatfromType(serviceDefinition, Platform.OPENSTACK);
		serviceInstanceRequest = ServiceInstanceRequestData.createServiceInstanceRequest(serviceDefinition.getId(),
				plan.getId());

		String serviceInstanceId = UUID.randomUUID().toString();
		mockMvc.perform(put("/v2/service_instances/" + serviceInstanceId)
				.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
				.param("accepts_incomplete", Boolean.TRUE.toString()).contentType(MediaType.APPLICATION_JSON)
				.content(ObjectMappingConverter.convertObjectToJsonBytes(serviceInstanceRequest)))
				.andExpect(status().isAccepted()).andDo(document("create-service-instance-example"));

		bindingRequest = InstanceBindingRequest.createBindingRequest(UUID.randomUUID().toString(),
				plan.getId().toString(), serviceDefinition.getId().toString());

		String contentAsString;
		do {
			MockHttpServletRequestBuilder header = get("/v2/service_instances/" + serviceInstanceId + "/last_operation")
					.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"));
			ResultActions perform = mockMvc.perform(header);
			contentAsString = perform.andReturn().getResponse().getContentAsString();
		} while (contentAsString.contains("\"state\":\"in progress\""));
		assertTrue(contentAsString.contains("\"state\":\"succeeded\""));

		String instanceBindingId = UUID.randomUUID().toString();
		mockMvc.perform(put("/v2/service_instances/" + serviceInstanceId + "/service_bindings/" + instanceBindingId)
				.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(ObjectMappingConverter.convertObjectToJsonBytes(bindingRequest)))
				.andExpect(status().isCreated()).andDo(document("create-service-instance-binding"));

		mockMvc.perform(delete("/v2/service_instances/" + serviceInstanceId + "/service_bindings/" + instanceBindingId)
				.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(ObjectMappingConverter.convertObjectToJsonBytes(new Object()))).andExpect(status().isOk())
				.andDo(document("delete-service-instance-binding"));

		mockMvc.perform(delete("/v2/service_instances/" + serviceInstanceId)
				.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(ObjectMappingConverter.convertObjectToJsonBytes(bindingRequest))).andExpect(status().isOk())
				.andDo(document("delete-service-instance-binding"));
	}

	@Test
	public void testInstanceCreationAsync() throws IOException, Exception {
		serviceInstanceRequest = ServiceInstanceRequestData.createServiceInstanceRequest(serviceDefinition.getId(),
				this.getPlanByPlatfromType(serviceDefinition, Platform.OPENSTACK).getId());

		mockMvc.perform(put("/service_instances/" + UUID.randomUUID().toString()).param("accepts_incomplete", "true")
				.header("Authorization", "Basic " + this.basicAuth("admin", "cloudfoundry"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(ObjectMappingConverter.convertObjectToJsonBytes(serviceInstanceRequest)))
				.andExpect(status().isAccepted()).andDo(document("create-service-instance-example"));
	}

}
