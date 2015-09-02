package de.evoila.cf.broker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The response sent to the cloud controller when a bind request is successful.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceBindingResponse {

	private Map<String, Object> credentials;

	private String syslogDrainUrl;

	public ServiceInstanceBindingResponse() {
	}

	/**
	 * @param credentials
	 */
	public ServiceInstanceBindingResponse(Map<String, Object> credentials) {
		super();
		this.credentials = credentials;
	}

	/**
	 * @param credentials
	 * @param syslogDrainUrl
	 */
	public ServiceInstanceBindingResponse(Map<String, Object> credentials, String syslogDrainUrl) {
		super();
		this.credentials = credentials;
		this.syslogDrainUrl = syslogDrainUrl;
	}

	@JsonSerialize
	@JsonProperty("credentials")
	public Map<String, Object> getCredentials() {
		return this.credentials;
	}

	@JsonSerialize
	@JsonProperty("syslog_drain_url")
	public String getSyslogDrainUrl() {
		return this.syslogDrainUrl;
	}

}
