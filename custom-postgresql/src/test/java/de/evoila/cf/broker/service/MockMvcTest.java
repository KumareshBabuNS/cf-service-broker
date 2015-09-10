/**
 * 
 */
package de.evoila.cf.broker.service;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import de.evoila.cf.config.security.CustomSecurityConfiguration;
import de.evoila.config.web.CustomMvcConfiguration;

/**
 * @author Johannes Hiemer.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@WebAppConfiguration
@ContextConfiguration(classes = { CustomSecurityConfiguration.class,
		CustomMvcConfiguration.class })
public abstract class MockMvcTest {
	
	protected MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private FilterChainProxy filterChainProxy;
	
	@Before
    public final void initMockMvc() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext)
        		.addFilter(filterChainProxy).build();
    }

}
