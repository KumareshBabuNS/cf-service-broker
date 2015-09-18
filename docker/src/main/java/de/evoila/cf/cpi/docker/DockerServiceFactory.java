package de.evoila.cf.cpi.docker;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig.DockerClientConfigBuilder;
import com.github.dockerjava.core.LocalDirectorySSLConfig;
import com.github.dockerjava.core.SSLConfig;

import de.evoila.cf.broker.service.PlatformService;

/**
 * 
 * @author Dennis Mueller, evoila GmbH, Aug 26, 2015
 *
 */
public abstract class DockerServiceFactory implements PlatformService {

	private static final int PORT = 2345;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${docker.offset}")
	private int offset;

	@Value("${docker.imageName}")
	private String imageName;

	@Value("${docker.containerPort}")
	private int containerPort;

	@Value("${docker.passwordEnv}")
	private String passwordEnv;

	@Value("${docker.usernameEnv}")
	private String usernameEnv;

	@Value("${docker.vhostEnv}")
	private String vHostEnv;

	private Map<String, Map<String, Object>> containerCredentialMap = new HashMap<String, Map<String, Object>>();

	@Value("${docker.certpath}")
	private String dockerCertPath;

	@Value("${docker.host}")
	private String dockerHost;

	@Value("${docker.port}")
	private String dockerPort;

	@Value("${docker.volume.service.port}")
	private String dockerVolumePort;

	@Autowired
	private DockerVolumeServiceBroker dockerVolumeServiceBroker;

	private DockerClient createClientInstance() {
		URL url = this.getClass().getResource("/docker/");

		SSLConfig sslConfig = new LocalDirectorySSLConfig(url.getPath());
		DockerClientConfig dockerClientConfig = new DockerClientConfigBuilder()
				.withSSLConfig(sslConfig)
				.withUri("https://" + dockerHost + ":" + dockerPort).build();
		return DockerClientBuilder.getInstance(dockerClientConfig).build();
	}

	private String getEnviornment(String key, String value) {
		return key + "='" + value + "'";
	}

	private CreateContainerResponse createContainer(String vhost,
			String username, String password) {
		DockerClient dockerClient = this.createClientInstance();
		// XXX: port mapping
		Binding binding = new Binding(PORT);
		ExposedPort exposedPort = new ExposedPort(this.containerPort);
		PortBinding portBinding = new PortBinding(binding, exposedPort);
		CreateContainerCmd containerCmd = dockerClient.createContainerCmd(
				imageName).withPortBindings(portBinding);
		if (usernameEnv != null) {
			containerCmd.withEnv(getEnviornment(usernameEnv, username));
		}
		if (passwordEnv != null) {
			containerCmd.withEnv(getEnviornment(passwordEnv, password));
		}
		if (vHostEnv != null) {
			containerCmd.withEnv(getEnviornment(vHostEnv, vhost));
		}
		logger.trace(containerCmd.toString());
		CreateContainerResponse container = containerCmd.exec();
		try {
			dockerClient.close();
		} catch (IOException e) {
			logger.warn("Cannot close docker client at creating docker container!");
		}
		return container;
	}

	private String getContainerVolumeHostPath(String containerId) {
		DockerClient dockerClient = this.createClientInstance();
		InspectContainerResponse i = dockerClient.inspectContainerCmd(
				containerId).exec();
		return i.getVolumes()[0].getHostPath();
	}

	private String getContainerNode(String containerId) {
		DockerClient dockerClient = this.createClientInstance();
		InspectContainerResponse i = dockerClient.inspectContainerCmd(
				containerId).exec();
		// TODO
		return i.getHostsPath();
	}

	private void startContainer(CreateContainerResponse container) {
		DockerClient dockerClient = this.createClientInstance();
		dockerClient.startContainerCmd(container.getId()).exec();

		try {
			dockerClient.close();
		} catch (IOException e) {
			logger.warn("Cannot close docker client at starting docker container!");
		}

	}

	public void killContainer(String containerId) {
		DockerClient dockerClient = createClientInstance();
		dockerClient.killContainerCmd(containerId);
		
		try {
			dockerClient.close();
		} catch (IOException e) {
			logger.warn("Cannot close docker client at killing docker container!");
		}
	}
	
	public void removeContainer(String containerId) {
		DockerClient dockerClient = createClientInstance();
		dockerClient.removeContainerCmd(containerId);
		
		try {
			dockerClient.close();
		} catch (IOException e) {
			logger.warn("Cannot close docker client at killing docker container!");
		}
	}

	public CreateContainerResponse createDockerContainer(String instanceId,
			int voluneSize, String vhost, String username, String password) {
		CreateContainerResponse container;
		try {
			container = this.createContainer(vhost, username, password);

		} catch (Exception e) {
			logger.error("Cannot create docker container");
			throw e;
		}
		logger.trace("Docker container '" + container.getId()
				+ "' created with: -p " + this.containerPort + ":" + "PORT"
				+ " -e " + this.vHostEnv + "='" + vhost + "' -e "
				+ this.usernameEnv + "='" + username + "' -e "
				+ this.passwordEnv + "='" + password + this.imageName);

		String mountPoint = this.getContainerNode(container.getId());
		String nodeName = this.getContainerVolumeHostPath(container.getId());
		try {
			this.dockerVolumeServiceBroker.createVolume(nodeName, mountPoint,
					offset + voluneSize);
		} catch (Exception e) {
			this.removeContainer(container.getId());
			e.printStackTrace();
			// TODO
			return null;
		}

		startContainer(container);

		Map<String, Object> credentials = new HashMap<String, Object>();
		// credentials.put("uri", getUri());
		credentials.put("hostname", dockerHost);
		credentials.put("port", DockerServiceFactory.PORT);
		credentials.put("name", container.getId());
		credentials.put("vhost", vhost);
		credentials.put("username", username);
		credentials.put("password", password);

		containerCredentialMap.put(container.getId(), credentials);

		return container;
	}

	public void removeDockerContainer(String containerId) {
		this.killContainer(containerId);
		String nodeName = this.getContainerNode(containerId);
		String mountPoint = this.getContainerVolumeHostPath(containerId);
		try {
			dockerVolumeServiceBroker.deleteVolume(nodeName, mountPoint);
		} catch (Exception e) {
			logger.error("Cannot delete volume");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.removeContainer(containerId);
	}

}
