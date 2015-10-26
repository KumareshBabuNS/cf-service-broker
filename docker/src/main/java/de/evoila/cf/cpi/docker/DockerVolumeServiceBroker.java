package de.evoila.cf.cpi.docker;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
	public final static String CREATE_TOPIC = "create";
	public final static String DELETE_TOPIC = "delete";

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
	public void init() throws MqttException, SocketException {
		client = new MqttAsyncClient(dockerVolumeServiceBroker, MqttClient.generateClientId(), new MqttDefaultFilePersistence());
		client.setCallback(mqttCallback);
		//TODO
//		Inet4Address inetAddress = (Inet4Address) NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement();
//		setSenderId(inetAddress.getCanonicalHostName());
		setSenderId("172.16.241.1");
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setKeepAliveInterval(5);
		IMqttToken t = client.connect(connOpts);
		while (!t.isComplete());
		if(t.getException() != null) throw t.getException();
		client.subscribe(DOCKER_TOPIC +"/"+SIP_TOPIC+"/" + getSenderId() + "/" + JOBS_TOPIC, 1);
		
	}
	
	public void createVolume(String nodeName, String mountPoint, int volumeSize)
			throws TimeoutException, MqttException, InterruptedException  {
		UUID jobId = UUID.randomUUID();
		String payload = "{\"jobId\" : \""+ jobId.toString()+"\", \"sipId\" : \""+getSenderId()+"\", \"mountPoint\" : \""+mountPoint+"\", \"volumeSize\" : \""+volumeSize+"\"}";
		publishPayloadToNode(nodeName, payload, CREATE_TOPIC);
		this.waitForJob(jobId);
		
	}

	private void waitForJob(UUID jobId) throws TimeoutException, InterruptedException {
		for (int i = 0; i < 12; i++) {
			if(jobStatus.containsKey(jobId) && jobStatus.get(jobId)!=JobStatus.PENDING) return;
			Thread.sleep(10000);
		}
		throw new TimeoutException("Job is taking too long!");
	}

	private void publishPayloadToNode(String nodeName, String payload, String topic)
			throws MqttException {
		MqttMessage message = new MqttMessage();
		message.setPayload(payload.getBytes());
		client.publish(DOCKER_TOPIC+"/"+nodeName+"/"+VOLUMES_TOPIC+"/"+topic, message );
	}
	
	public void deleteVolume(String nodeName, String mountPoint)
			throws TimeoutException, MqttException, InterruptedException {
		UUID jobId = UUID.randomUUID();
		String payload = "{\"jobId\" : \""+ jobId.toString()+"\", \"sipId\" : \""+getSenderId()+"\", \"mountPoint\" : \""+mountPoint+"\"}";
		publishPayloadToNode(nodeName, payload, DELETE_TOPIC);
			waitForJob(jobId);
		
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

}
