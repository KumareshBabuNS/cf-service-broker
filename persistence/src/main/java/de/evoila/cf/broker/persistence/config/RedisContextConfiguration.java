package de.evoila.cf.broker.persistence.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import de.evoila.cf.broker.model.ServiceInstance;
import redis.clients.jedis.Protocol;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Configuration
public class RedisContextConfiguration {

	@Value("${redis.host:'http://localhost'}")
	private String hostname;

	@Value("${redis.port:6379}")
	private int port;

	@Value("${redis.password:''}")
	private String password;

	@Bean
	public JedisConnectionFactory jedisConnFactory() {
		JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();

		jedisConnFactory.setUsePool(true);
		jedisConnFactory.setHostName(hostname);
		jedisConnFactory.setPort(port);
		jedisConnFactory.setTimeout(Protocol.DEFAULT_TIMEOUT);
		// jedisConnFactory.setPassword(password);

		return jedisConnFactory;
	}

	/**
	 * Sentinal Support
	 */

	// @Bean
	// public RedisConnectionFactory redisConnectionFactory() {
	// RedisSentinelConfiguration sentinelConfig = new
	// RedisSentinelConfiguration().master("mymaster")
	// .sentinel("127.0.0.1", 26379).sentinel("127.0.0.1", 26380);
	// return new JedisConnectionFactory(sentinelConfig);
	// }

	/**
	 * Serialization
	 */

	@Bean
	public StringRedisSerializer stringRedisSerializer() {
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		return stringRedisSerializer;
	}

	@Bean
	public JacksonJsonRedisSerializer<ServiceInstance> jacksonJsonRedisJsonSerializer() {
		JacksonJsonRedisSerializer<ServiceInstance> jacksonJsonRedisJsonSerializer = new JacksonJsonRedisSerializer<>(
				ServiceInstance.class);
		return jacksonJsonRedisJsonSerializer;
	}

	/**
	 * Template
	 * 
	 * @param jedisConnFactory
	 * @param stringRedisSerializer
	 * @param jacksonJsonRedisJsonSerializer
	 */

	@Bean
	@Autowired
	public RedisTemplate<String, ? extends Object> redisTemplate(RedisConnectionFactory jedisConnFactory,
			RedisSerializer<?> stringRedisSerializer, RedisSerializer<?> jacksonJsonRedisJsonSerializer) {
		RedisTemplate<String, ? extends Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnFactory);
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(jacksonJsonRedisJsonSerializer);
		// redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	/**
	 * Code for Transaction Support
	 */

	// @Bean
	// public PlatformTransactionManager transactionManager() throws
	// SQLException {
	// return new DataSourceTransactionManager(dataSource());
	// }

	// @Bean
	// public DataSource dataSource() throws SQLException {...}
}