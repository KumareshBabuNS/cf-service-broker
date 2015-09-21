package de.evoila.cf.broker.exception;

import java.io.IOException;

/**
 * General exception for underlying broker errors (like connectivity to the service
 * being brokered).
 * 
 * @author Johannes Hiemer.
 *
 */
public class ServiceBrokerException extends Exception {
	
	private static final long serialVersionUID = -5544859893499349135L;
	private String message;
	
	public ServiceBrokerException(String message) {
		this.message = message;
	}
	
	public ServiceBrokerException(String message, IOException e) {
		super(message, e);
	}

	public String getMessage() {
		return message;
	}
	
}