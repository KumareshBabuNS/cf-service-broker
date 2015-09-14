/// **
// *
// */
// package de.evoila.cf.broker.service.impl;
//
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// import org.springframework.beans.factory.BeanCreationException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
//
// import de.evoila.cf.broker.exception.ServiceBrokerException;
// import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
// import de.evoila.cf.broker.model.Plan;
// import de.evoila.cf.broker.model.Platform;
// import de.evoila.cf.broker.model.ServiceDefinition;
// import de.evoila.cf.broker.model.ServiceInstance;
// import de.evoila.cf.broker.repository.BindingRepository;
// import de.evoila.cf.broker.repository.PlatformRepository;
// import de.evoila.cf.broker.repository.ServiceDefinitionRepository;
// import de.evoila.cf.broker.repository.ServiceInstanceRepository;
// import de.evoila.cf.broker.service.PlatformService;
//
/// **
// * @author Johannes Hiemer.
// * @author Christian Brinker, evoila.
// *
// */
// @Service
// public class StorageService
// implements BindingRepository, PlatformRepository, ServiceInstanceRepository,
/// ServiceDefinitionRepository {
//
// // Service Definition
//
// @Autowired
// private ServiceDefinition serviceDefinition;
//
// // Depl
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#
// * getServiceDefinition()
// */
// @Override
// public ServiceDefinition getServiceDefinition() {
// return serviceDefinition;
// }
//
// // public Map<String, ServiceInstance> getServiceInstances() {
// // return serviceInstances;
// // }
//
// // Depl
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#
// * validateServiceId(java.lang.String)
// */
// @Override
// public void validateServiceId(String serviceDefinitionId) throws
/// ServiceDefinitionDoesNotExistException {
// if (!serviceDefinitionId.equals(serviceDefinition.getId())) {
// throw new ServiceDefinitionDoesNotExistException(serviceDefinitionId);
// }
// }
//
// // Depl + Bind
// /*
// * (non-Javadoc)
// *
// * @see
// * de.evoila.cf.broker.service.impl.ServiceDefinitionRepository#getPlan(java
// * .lang.String)
// */
// @Override
// public Plan getPlan(String planId) throws ServiceBrokerException {
// for (Plan currentPlan : serviceDefinition.getPlans()) {
// if (currentPlan.getId().equals(planId)) {
// return currentPlan;
// }
// }
// throw new ServiceBrokerException("Missing plan for id: " + planId);
// }
//
// // Binding
//
// private Map<String, String> internalBindingIdMapping = new
/// ConcurrentHashMap<String, String>();
//
// // public Map<String, String> getInternalBindingIdMapping() {
// // return internalBindingIdMapping;
// // }
//
// // Bind
// /*
// * (non-Javadoc)
// *
// * @see
// * de.evoila.cf.broker.service.impl.BindingRepository#getInternalBindingId(
// * java.lang.String)
// */
// @Override
// public String getInternalBindingId(String bindingId) {
// return this.internalBindingIdMapping.get(bindingId);
// }
//
// // Bind
// /*
// * (non-Javadoc)
// *
// * @see
// * de.evoila.cf.broker.service.impl.BindingRepository#addInternalBinding(
// * java.lang.String, java.lang.String)
// */
// @Override
// public void addInternalBinding(String bindingId, String id) {
// this.internalBindingIdMapping.put(bindingId, id);
// }
//
// // Bind
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.BindingRepository#
// * containsInternalBindingId(java.lang.String)
// */
// @Override
// public boolean containsInternalBindingId(String bindingId) {
// return this.internalBindingIdMapping.containsKey(bindingId);
// }
//
// // Platform services
//
// private Map<Platform, PlatformService> platformServices = new
/// ConcurrentHashMap<Platform, PlatformService>();
//
// // public Map<Platform, PlatformService> getPlatformServices() {
// // return platformServices;
// // }
//
// // Depl
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.PlatformRepositroy#addPlatform(de.
// * evoila.cf.broker.model.Platform,
// * de.evoila.cf.broker.service.PlatformService)
// */
// @Override
// public void addPlatform(Platform platform, PlatformService platformService) {
// if (platformServices.get(platform) == null)
// platformServices.put(platform, platformService);
// else
// throw new BeanCreationException("Cannot add multiple instances of platform
/// service to PlatformRepository");
// }
//
// // Depl
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.PlatformRepositroy#getPlatform(de.
// * evoila.cf.broker.model.Platform)
// */
// @Override
// public PlatformService getPlatformService(Platform platform) {
// return platformServices.get(platform);
// }
//
// // Service Instances
//
// private Map<String, ServiceInstance> serviceInstances = new
/// ConcurrentHashMap<String, ServiceInstance>();
//
// // Depl + Bind
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.ServiceInstanceRepository#
// * getServiceInstance(java.lang.String)
// */
// @Override
// public ServiceInstance getServiceInstance(String instanceId) {
// return this.serviceInstances.get(instanceId);
// }
//
// // Depl
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.ServiceInstanceRepository#
// * containsServiceInstanceId(java.lang.String)
// */
// @Override
// public boolean containsServiceInstanceId(String serviceInstanceId) {
// return this.serviceInstances.containsKey(serviceInstanceId);
// }
//
// // Depl + PGBindingTest
// /*
// * (non-Javadoc)
// *
// * @see de.evoila.cf.broker.service.impl.ServiceInstanceRepository#
// * addServiceInstance(java.lang.String,
// * de.evoila.cf.broker.model.ServiceInstance)
// */
// @Override
// public void addServiceInstance(String id, ServiceInstance serviceInstance) {
// serviceInstances.put(id, serviceInstance);
// }
//
// }
