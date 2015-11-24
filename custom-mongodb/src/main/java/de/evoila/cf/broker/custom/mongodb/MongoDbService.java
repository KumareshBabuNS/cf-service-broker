/**
 * 
 */
package de.evoila.cf.broker.custom.mongodb;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Johannes Hiemer
 *
 */
@Service
public class MongoDbService {
	
	private String host;
	
	private int port;
	
	private MongoClient mongoClient;

	public boolean isConnected() {
		return mongoClient != null && mongoClient.getUsedDatabases() != null;
	}

	public void createConnection(String id, String host, int port) throws UnknownHostException {
		this.host = host;
		this.port = port;
		
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
