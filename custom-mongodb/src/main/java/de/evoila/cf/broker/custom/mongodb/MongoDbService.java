/**
 * 
 */
package de.evoila.cf.broker.custom.mongodb;

import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;

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
		return mongoClient != null && mongoClient.listDatabases() != null;
	}

	public void createConnection(String id, String host, int port) {
		mongoClient = new MongoClient(host, port);
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
