package de.evoila.cf.cpi.docker;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.evoila.cf.cpi.docker.model.JobStatus;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

/**
 * 
 * @author Dennis Mueller.
 *
 */
@Service
public class VolumeBrokerCallback implements MqttCallback {
	
	@Autowired
	public DockerVolumeServiceBroker dockerVolumeServiceBroker; 
	
	@Override
	public void connectionLost(Throwable cause) {
		try {
			dockerVolumeServiceBroker.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		JSONObject json;
		try {
			json = (JSONObject) new JSONParser(JSONParser.ACCEPT_SIMPLE_QUOTE).parse(message.getPayload());
			UUID jobId = UUID.fromString((String) json.get("jobId"));
			JobStatus jobStatus = JobStatus.valueOf((String)json.get("status"));
			this.dockerVolumeServiceBroker.updateJobStatusById(jobId, jobStatus);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
