/**
 * 
 */
package de.evoila.cf.broker.service;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceDefinition;
import de.evoila.cf.config.security.CustomSecurityConfiguration;
import de.evoila.config.web.CustomMvcConfiguration;

/**
 * @author Johannes Hiemer.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { CustomSecurityConfiguration.class, CustomMvcConfiguration.class })
public abstract class MockMvcTest {

	protected MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy filterChainProxy;

	@Before
	public final void initMockMvc() throws Exception {
		mockMvc = webAppContextSetup(webApplicationContext).addFilter(filterChainProxy).build();
	}

	protected Plan getPlanByPlatfromType(ServiceDefinition serviceDefinition, Platform platform) {
		for (Plan plan : serviceDefinition.getPlans()) {
			if (plan.getPlatform().equals(platform)) {
				return plan;
			}
		}
		return null;
	}

	protected String basicAuth(String name, String password) {
		String authString = name + ":" + password;

		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());

		return new String(authEncBytes);
	}

}
