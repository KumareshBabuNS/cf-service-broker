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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import de.evoila.cf.broker.model.BaseEntity;

/**
 * @author Christian Brinker, evoila.
 *
 * @param <T>
 *            Entity type
 * @param <ID>
 *            ID type
 */
public class CrudRepositoryImpl<T extends BaseEntity<ID>, ID extends Serializable> implements CrudRepository<T, ID> {

	@Autowired
	private RedisTemplate<String, T> redisTemplate;

	@Override
	public long count() {
		return findAllAsList().size();
	}

	@Override
	public void delete(ID id) {
		String key = id.toString();
		redisTemplate.opsForValue().getOperations().delete(key);
	}

	@Override
	public void delete(T entity) {
		String key = entity.getId().toString();
		redisTemplate.opsForValue().getOperations().delete(key);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		delete(findAll());
	}

	@Override
	public boolean exists(ID id) {
		return findOne(id) != null;
	}

	@Override
	public Iterable<T> findAll() {
		return findAllAsList();
	}

	private List<T> findAllAsList() {
		List<T> entities = new ArrayList<>();

		Set<String> keys = redisTemplate.keys("*");
		Iterator<String> it = keys.iterator();

		while (it.hasNext()) {
			entities.add(findOne(it.next()));
		}

		return entities;
	}

	@Override
	public Iterable<T> findAll(Iterable<ID> ids) {
		List<String> keys = new ArrayList<String>();
		for (ID id : ids) {
			keys.add(id.toString());
		}
		return redisTemplate.opsForValue().multiGet(keys);
	}

	@Override
	public T findOne(ID id) {
		return findOne(id.toString());
	}

	private T findOne(String id) {
		return redisTemplate.opsForValue().get(id);
	}

	@Override
	public <S extends T> S save(S entity) {
		redisTemplate.opsForValue().set(entity.getId().toString(), entity);
		return entity;
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		for (S entity : entities) {
			save(entity);
		}
		return entities;
	}

}
