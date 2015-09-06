package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The response from the broker sent back to the cloud controller on a
 * successful service instance creation request
 * 
 * @author Johannes Hiemer.
 * 
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class CreateServiceInstanceProcessingResponse {
	
	/**
	 * Allowed values are:
	 *  "in progress"
	 *  "succeeded"
	 *  "failed"
	 */
	@JsonSerialize
	@JsonProperty("state")
	private String state;
	
	@JsonSerialize
	@JsonProperty("description")
	private String description;
	
	public CreateServiceInstanceProcessingResponse() {
		super();
	}

	public CreateServiceInstanceProcessingResponse(String state,
			String description) {
		super();
		this.state = state;
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
