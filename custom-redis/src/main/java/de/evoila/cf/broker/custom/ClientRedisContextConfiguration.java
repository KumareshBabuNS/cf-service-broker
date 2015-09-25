// package de.evoila.cf.broker.custom;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import
// org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
// import org.springframework.data.redis.serializer.StringRedisSerializer;
//
// import de.evoila.cf.broker.model.ServiceInstance;
// import redis.clients.jedis.Protocol;
//
/// **
// * @author Christian Brinker, evoila.
// *
// */
// @Configuration
// public class ClientRedisContextConfiguration {
//
// @Value("${redis.host:'http://localhost'}")
// private String hostname;
//
// @Value("${redis.port:6379}")
// private int port;
//
// @Value("${redis.password:''}")
// private String password;
//
// @Value("${redis.database}")
// private int database;
//
// @Bean
// public JedisConnectionFactory clientJedisConnFactory() {
// JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
//
// jedisConnFactory.setUsePool(true);
// jedisConnFactory.setHostName(hostname);
// jedisConnFactory.setDatabase(database);
// jedisConnFactory.setPort(port);
// jedisConnFactory.setTimeout(Protocol.DEFAULT_TIMEOUT);
//
// return jedisConnFactory;
// }
//
// /**
// * Template
// *
// * @param jedisConnFactory
// * @param stringRedisSerializer
// * @param jacksonJsonRedisJsonSerializer
// */
// @Bean
// public RedisTemplate<String, ? extends Object> clientRedisTemplate() {
// RedisTemplate<String, ? extends Object> redisTemplate = new
// RedisTemplate<>();
// redisTemplate.setConnectionFactory(clientJedisConnFactory());
// redisTemplate.setKeySerializer(new StringRedisSerializer());
// redisTemplate.setValueSerializer(new
// JacksonJsonRedisSerializer<>(ServiceInstance.class));
// return redisTemplate;
// }
//
// // /**
// // * Template
// // *
// // * @param jedisConnFactory
// // * @param stringRedisSerializer
// // * @param jacksonJsonRedisJsonSerializer
// // */
// // @Bean
// // @Autowired
// // public RedisTemplate<String, String> stringRedisTemplate() {
// // RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
// // redisTemplate.setConnectionFactory(jedisConnFactory());
// // redisTemplate.setKeySerializer(new StringRedisSerializer());
// // redisTemplate.setValueSerializer(new StringRedisSerializer());
// // return redisTemplate;
// // }
//
// }