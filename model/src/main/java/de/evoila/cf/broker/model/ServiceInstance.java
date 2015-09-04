package de.evoila.cf.broker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * An instance of a ServiceDefinition.
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstance {

	@JsonSerialize
	@JsonProperty("service_instance_id")
	private String id;

	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;

	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;

	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;

	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;

	@JsonIgnore
	private String dashboardUrl;

	@JsonSerialize
	@JsonProperty("parameters")
	private Map<String, String> parameters;

	@JsonIgnore
	private String internalId;

	@SuppressWarnings("unused")
	private ServiceInstance() {
	}

	public ServiceInstance(String id, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid, String dashboardUrl) {
		initialize(id, serviceDefinitionId, planId, organizationGuid, spaceGuid);
		setDashboardUrl(dashboardUrl);
	}

	private void initialize(String id, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
	}

	public ServiceInstance(String serviceInstanceId, String serviceDefintionId, String planId, String organizationGuid,
			String spaceGuid, String daschboardUrl, String internalId) {
		initialize(id, serviceDefinitionId, planId, organizationGuid, spaceGuid);
		setInternalId(internalId);
		setDashboardUrl(dashboardUrl);
	}

	public ServiceInstance(String serviceInstanceId, String serviceDefinitionId, String planId, String organizationGuid,
			String spaceGuid) {
		initialize(serviceInstanceId, serviceDefinitionId, planId, organizationGuid, spaceGuid);
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	private void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	private void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	private void setOrganizationGuid(String organizationGuid) {
		this.organizationGuid = organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	private void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	private void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

}
