package de.evoila.cf.broker.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The response from the broker sent back to the cloud controller on a
 * successful service instance creation request
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 * @author Christian Brinker, evoila.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class CreateServiceInstanceResponse {

	private String dashboardUrl;

	public CreateServiceInstanceResponse() {
	}

	public CreateServiceInstanceResponse(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	@JsonSerialize
	@JsonProperty("dashboard_url")
	public String getDashboardUrl() {
		return dashboardUrl;
	}

}
