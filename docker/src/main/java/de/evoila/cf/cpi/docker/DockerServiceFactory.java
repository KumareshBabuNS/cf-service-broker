package de.evoila.cf.cpi.docker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.github.dockerjava.api.DockerClient;
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

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServiceInstanceBindingResponse;
import de.evoila.cf.broker.model.ServiceInstanceCreationResult;
import de.evoila.cf.broker.service.impl.ServiceInstanceServiceImpl;

/**
 * 
 * @author Dennis Mueller, evoila GmbH, Aug 26, 2015
 *
 */
public abstract class DockerServiceFactory extends ServiceInstanceServiceImpl {

	private static final int PORT = 2345;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static int SMALL_SIZE = 25;
	private static int MEDIUM_SIZE = 2048;

	protected abstract String getType();

	protected abstract int getOffset();

	protected abstract String getImageName();

	protected abstract int getSevicePort();

	protected abstract String getContainerEnviornment();

	protected abstract String getContainerVolume();

	protected abstract String getPassword();

	protected abstract String getUsername();

	protected abstract String getVhost();
	
	private Map<String, Map<String, Object>> containerCredentialMap = new HashMap<String, Map<String,Object>>();

	@Value("${docker.certpath}")
	private String dockerCertPath;

	@Value("${docker.host}")
	private String dockerHost;

	@Value("${docker.port}")
	private String dockerPort;

	@Value("${docker.volume.service.port}")
	private String dockerVolumePort;

	@PostConstruct
	public void init() {

	}

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

	private String createDockerContainer(String imageName, int servicePort,
			String volumePath, String mountPoint, String env) {
		DockerClient dockerClient = this.createDockerClientInstance();
		// XXX: port mapping
		Binding binding = new Binding(PORT);
		ExposedPort exposedPort = new ExposedPort(servicePort);
		PortBinding portBinding = new PortBinding(binding, exposedPort);
		Volume volume = new Volume(volumePath);
		Bind bind = new Bind(mountPoint, volume);
		CreateContainerResponse container = dockerClient
				.createContainerCmd(imageName).withPortBindings(portBinding)
				.withVolumes(volume).withBinds(bind).withEnv(env)// ("POSTGRES_PASSWORD=123456")
				.exec();
		dockerClient.startContainerCmd(container.getId()).exec();
		try {
			dockerClient.close();
		} catch (IOException e) {
			logger.error("On docker client close: " + e.getMessage());
			e.printStackTrace();
		}
		return container.getId();
	}

	@Override
	public ServiceInstanceCreationResult provisionServiceInstance(String serviceInstanceId, String planId)
			throws ServiceBrokerException {

		Properties volume;
		switch (planId.toUpperCase()) {
		case "S":
			volume = this.createDockerVolume(SMALL_SIZE + getOffset());
			break;
		case "M":
			volume = this.createDockerVolume(MEDIUM_SIZE + getOffset());
			break;
		default:
			volume = this.createDockerVolume(SMALL_SIZE + getOffset());
			break;
		}
		String mountPoint = volume.getProperty("mountPoint");
		String volumeName = volume.getProperty("name");
		String containerId;
		try {
			containerId = this.createDockerContainer(getImageName(),
					getSevicePort(), getContainerVolume(), mountPoint,
					getContainerEnviornment());
		} catch (Exception e) {
			logger.error("Cannot create docker container");
			return null;
		}
		logger.trace("Docker container '" + containerId + "' created with: -p "
				+ getSevicePort() + ":" + "PORT" + " -v " + mountPoint + ":"
				+ getContainerVolume() + " -e " + getContainerEnviornment()
				+ " " + getImageName());
		ServiceInstanceCreationResult creationResult = new ServiceInstanceCreationResult();
		
		Map<String, Object> credentials = new HashMap<String, Object>();
		//credentials.put("uri", getUri());
		credentials.put("hostname", dockerHost);
		credentials.put("port", this.PORT);
		credentials.put("name", containerId);
		credentials.put("vhost", getVhost());
		credentials.put("username", getUsername());
		credentials.put("password", getPassword());
		
		containerCredentialMap.put(containerId, credentials); 
		
		creationResult.setDaschboardUrl(null);
		creationResult.setInternalId(containerId);
		return creationResult;
	}

	@Override
	public ServiceInstanceBindingResponse bindService(String insternalId)
			throws ServiceBrokerException {
		ServiceInstanceBindingResponse bindingResponse = new ServiceInstanceBindingResponse();

		bindingResponse.setCredentials(this.containerCredentialMap.get(insternalId));
		bindingResponse.setSyslogDrainUrl(null);
		return bindingResponse;
	}

	@Override
	public void deleteBinding(String internalId) throws ServiceBrokerException {

	}

}
