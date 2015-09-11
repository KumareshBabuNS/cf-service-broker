/**
 * 
 */
package de.evoila.cf.broker.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Service
public class JobProgressService {

	private static final String SUCCESS = "success";

	private static final String FAILED = "failed";

	private static final String IN_PROGRESS = "in progress";

	private Map<String, String> jobProgress = new ConcurrentHashMap<String, String>();

	public String getProgress(String serviceInstanceId) {
		return jobProgress.get(serviceInstanceId);
	}

	public void startJob(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, IN_PROGRESS);
	}

	public void failJob(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, FAILED);
	}

	public void succeedProgress(ServiceInstance serviceInstance) {
		changeStatus(serviceInstance, SUCCESS);
	}

	private void changeStatus(ServiceInstance serviceInstance, final String newStatus) {
		jobProgress.put(serviceInstance.getId(), newStatus);
	}
}
