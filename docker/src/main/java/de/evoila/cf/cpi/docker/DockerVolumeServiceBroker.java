package de.evoila.cf.cpi.docker;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.evoila.cf.cpi.docker.model.JobStatus;

@Service
public class DockerVolumeServiceBroker {

	public final static String DOCKER_TOPIC = "docker";
	public final static String SIP_TOPIC = "sip";
	public final static String VOLUMES_TOPIC = "volumes";
	public final static String JOBS_TOPIC = "jobs";

	@Value("${docker.volume.service.broker}")
	private String dockerVolumeServiceBroker;
	private MqttAsyncClient client;
	
	private Map<UUID, JobStatus> jobStatus = new ConcurrentHashMap<UUID, JobStatus>();
	
	public JobStatus getJobStatusById(UUID id) {
		return this.jobStatus.get(id);
	}
	
	public void updateJobStatusById(UUID id, JobStatus jobStatus) {
		this.jobStatus.put(id, jobStatus);
	}
	
	private String senderId;
	
	@Autowired
	private MqttCallback mqttCallback;
	
	@PostConstruct
	private void init() throws MqttException, SocketException{
		client = new MqttAsyncClient(dockerVolumeServiceBroker, MqttClient.generateClientId(), new MqttDefaultFilePersistence());
		Inet4Address inetAddress = (Inet4Address) NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement();
		setSenderId(inetAddress.getCanonicalHostName());
		client.subscribe(SIP_TOPIC+"/" + getSenderId() + "/" + JOBS_TOPIC, 1);
		
		client.setCallback(mqttCallback);
	}
	
	public void createVolume(String nodeName, String mountPoint, int volumeSize)
			throws TimeoutException, MqttException  {
		UUID jobId = UUID.randomUUID();
		String payload = "{jobId : "+ jobId.toString()+", sipId : "+getSenderId()+", mountPoint : "+mountPoint+", volumeSize : "+volumeSize+"}";
		publishPayloadToNode(nodeName, payload);
			this.waitForJob(jobId);
		
	}

	private void waitForJob(UUID jobId) throws TimeoutException {
		for (int i = 0; i < 12; i++) {
			if(jobStatus.get(jobId)!=JobStatus.PENDING) return;
		}
		throw new TimeoutException("Job is taking too long!");
	}

	private void publishPayloadToNode(String nodeName, String payload)
			throws MqttException {
		MqttMessage message = new MqttMessage();
		message.setPayload(payload.getBytes());
		client.publish(DOCKER_TOPIC+"/"+nodeName+"/"+VOLUMES_TOPIC, message );
	}
	
	public void deleteVolume(String nodeName, String mountPoint)
			throws TimeoutException, MqttException {
		UUID jobId = UUID.randomUUID();
		String payload = "{jobId : "+ jobId.toString()+", sipId : "+getSenderId()+", mountPoint : "+mountPoint+"}";
		publishPayloadToNode(nodeName, payload);
			waitForJob(jobId);
		
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

}
