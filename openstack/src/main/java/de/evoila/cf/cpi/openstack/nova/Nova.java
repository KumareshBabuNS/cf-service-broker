package de.evoila.cf.cpi.openstack.nova;

import com.woorea.openstack.nova.model.Flavor;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.Image;
import com.woorea.openstack.nova.model.Images;
import com.woorea.openstack.nova.model.KeyPair;
import com.woorea.openstack.nova.model.KeyPairs;
import com.woorea.openstack.nova.model.SecurityGroup;
import com.woorea.openstack.nova.model.SecurityGroups;
import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.ServerForCreate;
import com.woorea.openstack.nova.model.Servers;
import com.woorea.openstack.nova.model.Snapshot;
import com.woorea.openstack.nova.model.Snapshots;
import com.woorea.openstack.nova.model.Volume;
import com.woorea.openstack.nova.model.VolumeForCreate;
import com.woorea.openstack.nova.model.Volumes;

import de.evoila.cf.cpi.openstack.NovaFactory;

/**
 * 
 * @author Johannes Hiemer, cloudscale
 *
 */
public class Nova extends NovaFactory {	
	
	public KeyPairs getKeyPairs() {
		return novaClient.keyPairs().list().execute();
	}	
	
	public void deleteKeyPair(String name) {
		novaClient.keyPairs().delete(name).execute();
	}
	
	public KeyPair createKeyPair(String name, String publicKey) {
		if (publicKey != null)
			return novaClient.keyPairs().create(name, publicKey).execute();
		else
			return novaClient.keyPairs().create(name).execute();
	}
	
	public Server createInstance(String name, Flavor flavor, Image image, 
			KeyPair keypair, String securityGroupName, int count) {
		ServerForCreate serverForCreate = new ServerForCreate();
		serverForCreate.setName(name);
		serverForCreate.setFlavorRef(flavor.getId());
		serverForCreate.setImageRef(image.getId());
		serverForCreate.setKeyName(keypair.getName());
		serverForCreate.getSecurityGroups().add(
				new ServerForCreate.SecurityGroup(securityGroupName));
		serverForCreate.setMin(1);
		serverForCreate.setMax(count);

		Server server = novaClient.servers().boot(serverForCreate).execute();
		return server;
	}
	
	public Server createInstance(ServerForCreate serverForCreate) {
		Server server = novaClient.servers().boot(serverForCreate).execute();
		return server;
	}
	
	public Servers getInstances() {
		return novaClient.servers().list(true).execute();
	}
	
	public Server getInstance(String id) {
		return novaClient.servers().show(id).execute();
	}
	
	public void startInstance(String id) {
		novaClient.servers().start(id).execute();
	}
	
	public void stopInstance(String id) {
		novaClient.servers().stop(id).execute();
	}
	
	public void lockInstance(String id) {
		novaClient.servers().lock(id).execute();
	}
	
	public void unlockInstance(String id) {
		novaClient.servers().unlock(id).execute();
	}
	
	public void deleteInstance(String id) {
		novaClient.servers().delete(id).execute();
	}
	
	public Volume createVolume(String name, String description, Integer size, String availabilityZone) {
		VolumeForCreate volumeForCreate = new VolumeForCreate();
		volumeForCreate.setName(name);
		volumeForCreate.setDescription(description);
		volumeForCreate.setSize(size);
		volumeForCreate.setAvailabilityZone(availabilityZone);
		return novaClient.volumes().create(volumeForCreate).execute();
	}
	
	public Volume createVolume(VolumeForCreate volumeForCreate) {
		return novaClient.volumes().create(volumeForCreate).execute();
	}
	
	public void deleteVolume(String id) {
		novaClient.volumes().delete(id).execute();
	}
	
	public Volumes getVolumes() {
		return novaClient.volumes().list(true).execute();
	}
	
	public Volume getVolume(String id) {
		return novaClient.volumes().show(id).execute();
	}
	
	public void attachVolumeToInstance(String serverId, String volumeId, String device) {
		novaClient.servers().attachVolume(serverId, volumeId, device).execute();
	}
	
	public void detachVolumeFromInstance(String id, String volumeId, String device) {
		novaClient.servers().detachVolume(id, volumeId).execute();
	}
	
	public SecurityGroups getSecurityGroups() {
		return novaClient.securityGroups().listSecurityGroups().execute();
	}
	
	public SecurityGroup getSecurityGroup(Integer id) {
		return novaClient.securityGroups().showSecurityGroup(id).execute();
	}
	
	public void deleteSecurityGroup(Integer id) {
		novaClient.securityGroups().deleteSecurityGroup(id).execute();
	}
	
	public SecurityGroup createSecurityGroup(String name, String description) {
		return novaClient.securityGroups().createSecurityGroup(name, description).execute();
	}
	
	public void addSecurityGroupRules(Integer parentSecurityGroupId, String ipProtocol, 
			Integer fromPort, Integer toPort, String sourceGroupId) {
		novaClient.securityGroups().createSecurityGroupRule(parentSecurityGroupId, 
				ipProtocol, fromPort, toPort, sourceGroupId);
	}
	
	public void deleteSecurityGroupRules(Integer id) {
		novaClient.securityGroups().deleteSecurityGroupRule(id);
	}
	
	public Flavors getFlavors() {
		return novaClient.flavors().list(true).execute();
	}
	
	public Flavor getFlavor(String id) {
		return novaClient.flavors().show(id).execute();
	}
	
	public Images getImages() {
		return novaClient.images().list(true).execute();
	}
	
	public Image getImage(String id) {
		return novaClient.images().show(id).execute();
	}
	
	public Snapshots getSnapshots() {
		return novaClient.snapshots().list(true).execute();
	}
	
	public Snapshot getSnapshot(String id) {
		return novaClient.snapshots().show(id).execute();
	}
	
	public void createSnapshot(Snapshot snapshotCreateFor) {		
		novaClient.snapshots().create(snapshotCreateFor).execute();
	}
	
}
