/**
 * 
 */
package de.evoila.cf.broker.service.impl.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author Johannes Hiemer.
 * 
 */
@Service
public class MongoDBAdminService {

	public static final String ADMIN_DB = "admin";

	@Autowired
	private MongoClient mongoClient;

	public boolean databaseExists(String database) {
		return mongoClient.getDatabaseNames().contains(database);
	}

	public void deleteDatabase(String database) {
		mongoClient.getDB(ADMIN_DB);
		mongoClient.dropDatabase(database);
	}

	public DB createDatabase(String database) {
		DB db = mongoClient.getDB(database);

		DBCollection dbCollection = db.createCollection("init", null);
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("init", "0");
		dbCollection.insert(basicDBObject);
		dbCollection.drop();

		return db;

	}

	public void createUser(String database, String username, String password) {
		DB db = mongoClient.getDB(database);
		db.addUser(username, password.toCharArray());

	}

	public void deleteUser(String database, String username) {
		DB db = mongoClient.getDB(database);
		db.removeUser(username);
	}

	public String getConnectionString(String database, String username,
			String password) {
		StringBuilder builder = new StringBuilder();
		builder.append("mongodb://");
		builder.append(username);
		builder.append(":");
		builder.append(password);
		builder.append("@");
		builder.append(getServerAddresses());
		builder.append("/");
		builder.append(database);
		return builder.toString();
	}

	public String getServerAddresses() {
		StringBuilder builder = new StringBuilder();
		for (ServerAddress address : mongoClient.getAllAddress()) {
			builder.append(address.getHost());
			builder.append(":");
			builder.append(address.getPort());
			builder.append(",");
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
}
