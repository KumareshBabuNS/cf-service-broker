/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.List;

import org.openstack4j.model.compute.Server;
import org.openstack4j.model.heat.Stack;
import org.openstack4j.model.network.Subnet;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import de.evoila.cf.cpi.openstack.fluent.NeutronFluent;
import de.evoila.cf.cpi.openstack.fluent.NovaFluent;

/**
 * @author Christian Brinker, evoila.
 *
 */
public class DefaultIpAccessor extends IpAccessor {

	private String networkId;

	private String subnetId;

	private NovaFluent novaFluent;

	private NeutronFluent neutronFluent;

	private HeatFluent heatFluent;

	/**
	 * @param networkId
	 * @param subnetId
	 * @param novaFluent
	 * @param neutronFluent
	 * @param heatFluent
	 */
	public DefaultIpAccessor(String networkId, String subnetId) {
		super();
		this.networkId = networkId;
		this.subnetId = subnetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.evoila.cf.cpi.openstack.custom.IpAccessor#getIpAddresses(org.
	 * openstack4j.model.heat.Stack, java.lang.String)
	 */
	@Override
	public List<ServerAddress> getIpAddresses(String instanceId) throws PlatformException {
		Subnet subnet = neutronFluent.subnet(networkId, subnetId);
		final String ip = novaFluent.ip(this.details(instanceId).get(0), subnet.getName());
		return Lists.newArrayList(new ServerAddress("default", ip));
	}

	protected List<Server> details(String instanceId) throws PlatformException {
		List<Server> servers = servers(instanceId);

		if (servers.size() >= 1)
			return servers;
		else
			return null;
	}

	private List<Server> servers(String instanceId) throws PlatformException {
		Stack stack = heatFluent.get(HeatFluent.uniqueName(instanceId));

		return heatFluent.servers(stack.getName(), stack.getId(), HeatFluent.NOVA_INSTANCE_TYPE);
	}

	@Autowired
	public void setNovaFluent(NovaFluent novaFluent) {
		this.novaFluent = novaFluent;
	}

	@Autowired
	public void setNeutronFluent(NeutronFluent neutronFluent) {
		this.neutronFluent = neutronFluent;
	}

	@Autowired
	public void setHeatFluent(HeatFluent heatFluent) {
		this.heatFluent = heatFluent;
	}
}
