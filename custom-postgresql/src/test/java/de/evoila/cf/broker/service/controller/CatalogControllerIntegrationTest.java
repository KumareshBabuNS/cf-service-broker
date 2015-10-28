/**
 * 
 */
package de.evoila.cf.broker.service.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.evoila.cf.broker.cpi.endpoint.controller.EndpointController;
import de.evoila.cf.broker.model.fixture.CatalogFixture;
import de.evoila.cf.broker.model.fixture.ServiceFixture;
import de.evoila.cf.broker.service.CatalogService;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
public class CatalogControllerIntegrationTest {
	
	private MockMvc mockMvc;

	@InjectMocks
	private EndpointController controller;

	@Mock
	private CatalogService catalogService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

	    this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
	            .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
	}
	
	@Test
	public void catalogIsRetrievedCorrectly() throws Exception {
	    when(catalogService.getCatalog()).thenReturn(CatalogFixture.getCatalog());
	
	    this.mockMvc.perform(get("/v2/catalog")
	        .accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.services.", hasSize(1)))
            .andExpect(jsonPath("$.services[*].id", containsInAnyOrder(ServiceFixture.getService().getId())));
	    
	    // TO DO - check rest of the json including plans
	}

}
