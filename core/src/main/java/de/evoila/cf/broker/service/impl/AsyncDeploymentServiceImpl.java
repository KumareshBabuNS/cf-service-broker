package de.evoila.cf.broker.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.exception.ServiceInstanceDoesNotExistException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.service.AsyncDeploymentService;
import de.evoila.cf.broker.service.PlatformService;

@Service
public class AsyncDeploymentServiceImpl implements AsyncDeploymentService {
	
	private static final String SUCCESS = "success";

	private static final String FAILED = "failed";

	private static final String IN_PROGRESS = "in progress";

	private Map<String, String> jobProgress = new ConcurrentHashMap<String, String>();
	
	@Autowired
	private DeploymentServiceImpl deploymentService;
	
	@Async
	@Override
	public void asyncCreateInstance(ServiceInstance serviceInstance, Plan plan, PlatformService platformService) {
		jobProgress.put(serviceInstance.getId(), IN_PROGRESS);

		try {
			deploymentService.syncCreateInstance(serviceInstance, plan, platformService);
		} catch (ServiceBrokerException e) {
			jobProgress.put(serviceInstance.getId(), FAILED);
			e.printStackTrace();
			return;
		}

		jobProgress.put(serviceInstance.getId(), SUCCESS);
	}
	
	@Async
	@Override
	public void asyncDeleteInstance(String instanceId, ServiceInstance serviceInstance,
			PlatformService platformService) throws ServiceInstanceDoesNotExistException {
		jobProgress.put(instanceId, IN_PROGRESS);

		try {
			deploymentService.syncDeleteInstance(serviceInstance, platformService);
		} catch (ServiceBrokerException e) {
			jobProgress.put(instanceId, FAILED);
			e.printStackTrace();
			return;
		}

		jobProgress.put(instanceId, SUCCESS);
	}

	@Override
	public String getProgress(String serviceInstanceId) {
		return jobProgress.get(serviceInstanceId);
	}

}
