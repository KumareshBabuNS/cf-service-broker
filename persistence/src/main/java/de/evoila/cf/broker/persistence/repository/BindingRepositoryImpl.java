/**
 * 
 */
package de.evoila.cf.broker.persistence.repository;

import org.springframework.stereotype.Repository;

import de.evoila.cf.broker.repository.BindingRepository;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Repository
public class BindingRepositoryImpl extends StringCrudRepositoryImpl implements BindingRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.repository.BindingRepository#getInternalBindingId(
	 * java.lang.String)
	 */
	@Override
	public String getInternalBindingId(String bindingId) {
		return this.findOne(bindingId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.repository.BindingRepository#addInternalBinding(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public void addInternalBinding(String bindingId, String id) {
		save(bindingId, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.broker.repository.BindingRepository#
	 * containsInternalBindingId(java.lang.String)
	 */
	@Override
	public boolean containsInternalBindingId(String bindingId) {
		return exists(bindingId);
	}

}
