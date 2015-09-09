package de.evoila.cf.cpi.docker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.api.model.Volume;
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

	@Value("${docker.containerVolume}")
	private String containerVolume;

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

	private DockerClient createDockerClientInstance() {

		SSLConfig sslConfig = new LocalDirectorySSLConfig(dockerCertPath);
		DockerClientConfig dockerClientConfig = new DockerClientConfigBuilder()
				.withSSLConfig(sslConfig)
				.withUri("https://" + dockerHost + ":" + dockerPort).build();
		return DockerClientBuilder.getInstance(dockerClientConfig).build();
	}

	private Properties createDockerVolume(int volumeSize) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject("http://" + dockerHost + ":"
				+ dockerVolumePort + "/create/volume/" + volumeSize,
				Properties.class);
	}

	private String getEnviornment(String key, String value) {
		return key + "='" + value + "'";
	}

	private CreateContainerResponse createDockerContainer(String mountPoint, String vhost,
			String username, String password) {
		DockerClient dockerClient = this.createDockerClientInstance();
		// XXX: port mapping
		Binding binding = new Binding(PORT);
		ExposedPort exposedPort = new ExposedPort(this.containerPort);
		PortBinding portBinding = new PortBinding(binding, exposedPort);
		Volume volume = new Volume(this.containerVolume);
		Bind bind = new Bind(mountPoint + "/_data", volume);
		CreateContainerCmd containerCmd = dockerClient
				.createContainerCmd(imageName).withPortBindings(portBinding)
				.withVolumes(volume).withBinds(bind);
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
		dockerClient.startContainerCmd(container.getId()).exec();
		try {
			dockerClient.close();
		} catch (IOException e) {
			logger.error("On docker client close: " + e.getMessage());
			e.printStackTrace();
		}
		return container;
	}

	public CreateContainerResponse createDockerContainer(String instanceId, int voluneSize, String vhost, String username, String password) {
		Properties volume = this.createDockerVolume(voluneSize
				+ this.offset);
		String mountPoint = volume.getProperty("mountPoint");

		@SuppressWarnings("unused")
		String volumeName = volume.getProperty("name");

		CreateContainerResponse container;
		try {
			container = this.createDockerContainer(mountPoint, vhost,
					username, password);
		} catch (Exception e) {
			logger.error("Cannot create docker container");
			throw e;
		}
		logger.trace("Docker container '" + container.getId() + "' created with: -p "
				+ this.containerPort + ":" + "PORT" + " -v " + mountPoint + ":"
				+ this.containerVolume + " -e " + this.vHostEnv + "='" + vhost
				+ "' -e " + this.usernameEnv + "='" + username + "' -e "
				+ this.passwordEnv + "='" + password + this.imageName);

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

}
