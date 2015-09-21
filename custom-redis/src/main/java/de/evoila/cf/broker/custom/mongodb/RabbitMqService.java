/**
 * 
 */
package de.evoila.cf.broker.custom.mongodb;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.rabbitmq.client.impl.AMQConnection;

/**
 * @author Johannes Hiemer
 *
 */
@Service
public class RabbitMqService {
	
	private String host;
	
	private int port;
	
	private AMQConnection amqConnection;

	public boolean isConnected() {
		return mongoClient != null && mongoClient.listDatabases() != null;
	}

	public void createConnection(String id, String host, int port) {
		amqConnection = new Am
		MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(id, "admin", id.toCharArray());
		mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(mongoCredential));
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public MongoClient mongoClient() {
		return mongoClient;
	}

}
