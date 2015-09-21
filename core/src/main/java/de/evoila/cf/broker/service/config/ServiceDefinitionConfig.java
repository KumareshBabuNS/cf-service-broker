package de.evoila.cf.broker.service.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.evoila.cf.broker.controller.BaseController;
import de.evoila.cf.broker.controller.CatalogController;

@Controller
@RequestMapping(value = "/v2/test")
public class ServiceDefinitionConfig extends BaseController {

	@Autowired
	private ApplicationContext ctx;

	private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

	@Value(value = "${test}")
	private List<String> test;

	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET)
	public @ResponseBody List<String> getCatalog() {
		logger.debug("GET: getCatalog()");
		return test;
	}

}
