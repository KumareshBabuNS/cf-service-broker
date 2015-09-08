package de.evoila.cf.broker.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A service plan available for a ServiceDefinition
 * 
 * @author sgreenberg@gopivotal.com
 * @author Johannes Hiemer.
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Plan {

	@JsonSerialize
	@JsonProperty("id")
	private String id;
	
	@JsonSerialize
	@JsonProperty("name")
	private String name;
	
	@JsonSerialize
	@JsonProperty("description")
	private String description;
	
	@JsonSerialize
	@JsonProperty("metadata")
	private Map<String,Object> metadata = new HashMap<String,Object>();
	
	private int volumeSize;
	
	private Platform platform;
	
	private String flavorId;
	
	private int connections;
	
	public int getVolumeSize() {
		return volumeSize;
	}

	public Platform getPlatform() {
		return platform;
	}

	public String getFlavorId() {
		return flavorId;
	}

	public int getConnections() {
		return connections;
	}

	public Plan(String id, String name, String description, Platform platform, int volumeSize, String flavorId, int connections) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.platform = platform;
		this.volumeSize = volumeSize;
		this.flavorId = flavorId;
		this.connections = connections;
	}

	public Plan(String id, String name, String description, Map<String,Object> metadata, Platform platform, int volumeSize, String flavor, int connections) {
		this(id, name, description, platform, volumeSize, flavor, connections);
		setMetadata(metadata);
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}
	
	private void setMetadata(Map<String, Object> metadata) {
		if (metadata == null) {
			this.metadata = new HashMap<String,Object>();
		} else {
			this.metadata = metadata;
		}
	}
	
}
