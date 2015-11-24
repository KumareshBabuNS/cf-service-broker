/**
 * 
 */
package de.evoila.cf.broker.persistence.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import de.evoila.cf.broker.model.BaseEntity;

/**
 * @author Christian Brinker, evoila.
 *
 * @param <T>
 *            Entity type
 * @param <ID>
 *            ID type
 */
public class CrudRepositoryImpl<T extends BaseEntity<ID>, ID extends Serializable> {

	@Autowired
	@Qualifier("jacksonRedisTemplate")
	private RedisTemplate<String, T> redisTemplate;

	public long count() {
		return findAllAsList().size();
	}

	public void delete(ID id) {
		String key = generateKey(id);
		redisTemplate.opsForValue().getOperations().delete(key);
	}

	private String generateKey(ID id) {
		String key = getPrefix() + id.toString();
		return key;
	}

	protected String getPrefix() {
		return "";
	}

	public void delete(T entity) {
		ID id = entity.getId();
		String key = generateKey(id);
		redisTemplate.opsForValue().getOperations().delete(key);
	}

	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	public void deleteAll() {
		delete(findAll());
	}

	public boolean exists(ID id) {
		return findOne(id) != null;
	}

	public Iterable<T> findAll() {
		return findAllAsList();
	}

	private List<T> findAllAsList() {
		List<T> entities = new ArrayList<>();

		Set<String> keys = redisTemplate.keys(getPrefix() + "*");
		Iterator<String> it = keys.iterator();

		while (it.hasNext()) {
			entities.add(findOne(it.next()));
		}

		return entities;
	}

	public Iterable<T> findAll(Iterable<ID> ids) {
		List<String> keys = new ArrayList<String>();
		for (ID id : ids) {
			keys.add(generateKey(id));
		}
		return redisTemplate.opsForValue().multiGet(keys);
	}

	public T findOne(ID id) {
		String key = generateKey(id);
		return findOne(key);
	}

	private T findOne(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public <S extends T> S save(S entity) {
		ID id = entity.getId();
		redisTemplate.opsForValue().set(generateKey(id), entity);
		return entity;
	}

	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		for (S entity : entities) {
			save(entity);
		}
		return entities;
	}

}
