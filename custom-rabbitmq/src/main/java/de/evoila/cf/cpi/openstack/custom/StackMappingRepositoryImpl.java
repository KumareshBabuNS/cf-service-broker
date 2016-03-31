package de.evoila.cf.cpi.openstack.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.persistence.repository.CrudRepositoryImpl;
import de.evoila.cf.broker.repository.ServiceInstanceRepository;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Repository
public class StackMappingRepositoryImpl extends CrudRepositoryImpl<ServiceInstance, String> {
	
	@Autowired
	@Qualifier("jacksonStackMappingRedisTemplate")
	private RedisTemplate<String, ServiceInstance> redisTemplate;
	
	@Override
	protected RedisTemplate<String, ServiceInstance> getRedisTemplate() {
		return this.redisTemplate;
	}
	
	private static final String PREFIX = "stackmapping-";

	@Override
	protected String getPrefix() {
		return PREFIX;
	}

}
