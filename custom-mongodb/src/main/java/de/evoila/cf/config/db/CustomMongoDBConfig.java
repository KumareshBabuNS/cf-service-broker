package de.evoila.cf.config.db;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * 
 * @author Johannes Hiemer, cloudscale.
 *
 */
@Configuration
@Import(CustomPropertiesResolver.class)
@EnableMongoRepositories(value = "de.evoila.cf.broker.mongodb.repositories")
public class CustomMongoDBConfig extends AbstractMongoConfiguration {

	@Value("${database.nosql.host}")
	private String databaseHost;
	
	@Value("${database.nosql.user}")
	private String databaseUsername;
	
	@Value("${database.nosql.password}")
	private String databasePassword;
	
	@Value("${database.nosql.port}")
	private int databasePort;
	
	@Value("${database.nosql.database}")
	private String databaseName;
	
	@Override
	protected String getDatabaseName() {
		return databaseName;
	}
	
	@Override
	protected UserCredentials getUserCredentials() {
		return new UserCredentials(databaseUsername, databasePassword);
	}

	@Override
    protected String getMappingBasePackage() {
        return "de.evoila.cf.broker.model";
    }

	@Override
	@SuppressWarnings("deprecation")
	public Mongo mongo() throws Exception {
		return new Mongo(databaseHost, databasePort);
	}
	
	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		return new MongoClient();
	}
	
}