/**
 * 
 */
package de.evoila.cf.broker.postgresql.documentation.service;

import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.Test;
import org.springframework.http.MediaType;

import de.evoila.cf.broker.postresql.MockMvcTest;
import de.evoila.cf.broker.postresql.ObjectMappingConverter;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class ServiceInstanceControllerDocumentation extends MockMvcTest {

	@Test
	public void testLogin() throws IOException, Exception {
		mockMvc
			.perform(post("/j_spring_security_check")
					.contentType(MediaType.APPLICATION_JSON)
					.content(ObjectMappingConverter.convertObjectToJsonBytes(null))
			).andExpect(status().isOk())
			.andDo(document("user-login-example"));
	}
	
}
