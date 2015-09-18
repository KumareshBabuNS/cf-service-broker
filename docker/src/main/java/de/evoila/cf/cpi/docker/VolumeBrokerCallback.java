package de.evoila.cf.cpi.docker;

import java.util.UUID;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.cpi.docker.model.JobStatus;

@Service
public class VolumeBrokerCallback implements MqttCallback {
	
	@Autowired
	public DockerVolumeServiceBroker dockerVolumeServiceBroker; 
	
	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String[] splits = topic.split("/");
		String jobId = splits[splits.length];
		JSONObject json = (JSONObject) new JSONParser().parse(message.getPayload());
		this.dockerVolumeServiceBroker.updateJobStatusById(UUID.fromString(jobId), JobStatus.valueOf((String)json.get("status")));
	}

	
	
}
