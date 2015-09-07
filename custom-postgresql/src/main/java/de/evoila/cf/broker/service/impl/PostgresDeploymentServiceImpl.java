/**
 * 
 */
package de.evoila.cf.broker.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.service.postgres.PostgresSqlImplementation;
import de.evoila.cf.broker.service.postgres.PostgresSqlRoleImplementation;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Service
public class PostgresDeploymentServiceImpl extends AbstractDeploymentServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PostgresSqlImplementation postgresSqlImplementation;

	@Autowired
	private PostgresSqlRoleImplementation postgresSqlRoleImplementation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.AbstractDeploymentServiceImpl#
	 * isSyncPossibleOnCreate(de.evoila.cf.broker.model.Plan)
	 */
	@Override
	protected boolean isSyncPossibleOnCreate(Plan plan) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.AbstractDeploymentServiceImpl#
	 * postProvisioning(de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.Plan)
	 */
	@Override
	protected ServiceInstance postProvisioning(ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {
		String instanceId = serviceInstance.getId();

		try {
			postgresSqlImplementation.executeUpdate("CREATE DATABASE \"" + instanceId + "\" ENCODING 'UTF8'");
			postgresSqlImplementation.executeUpdate("REVOKE all on database \"" + instanceId + "\" from public");
		} catch (SQLException e) {
			log.error(e.toString());
			throw new ServiceBrokerException(e.getMessage());
		}

		return serviceInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.AbstractDeploymentServiceImpl#
	 * isSyncPossibleOnDelete(de.evoila.cf.broker.model.ServiceInstance)
	 */
	@Override
	protected boolean isSyncPossibleOnDelete(ServiceInstance instance) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.AbstractDeploymentServiceImpl#
	 * preDeprovisionServiceInstance(de.evoila.cf.broker.model.ServiceInstance)
	 */
	@Override
	protected void preDeprovisionServiceInstance(ServiceInstance serviceInstance) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.AbstractDeploymentServiceImpl#
	 * bindService(de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.Plan)
	 */
	@Override
	protected ServiceInstanceBindingResponse bindService(ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {
		String passwd = "";

		try {
			passwd = postgresSqlRoleImplementation.bindRoleToDatabase(serviceInstance.getId());
		} catch (SQLException e) {
			log.error(e.toString());
		}

		String dbURL = String.format("postgres://%s:%s@%s:%d/%s", serviceInstance.getId(), passwd,
				postgresSqlImplementation.getHost(), postgresSqlImplementation.getPort(), serviceInstance.getId());

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return new ServiceInstanceBindingResponse(credentials);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.service.impl.AbstractDeploymentServiceImpl#
	 * deleteBinding(java.lang.String)
	 */
	@Override
	protected void deleteBinding(ServiceInstance serviceInstance) throws ServiceBrokerException {
		try {
			postgresSqlRoleImplementation.unBindRoleFromDatabase(serviceInstance.getId());
		} catch (SQLException e) {
			log.error(e.toString());
		}
	}

}
