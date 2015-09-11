package de.evoila.cf.broker.repository;

/**
 * @author Christian Brinker, evoila.
 *
 */
public interface BindingRepository {

	// Bind
	String getInternalBindingId(String bindingId);

	// Bind
	void addInternalBinding(String bindingId, String id);

	// Bind
	boolean containsInternalBindingId(String bindingId);

}