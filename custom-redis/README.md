cloudfoundry-service-broker for MongoDB
===========================

Spring MVC based REST application for CloudFoundry V2 service brokers based on a suggestion by Steven Greenberg at https://github.com/cloudfoundry-community/spring-service-broker.

The enhancements are:

- Migration to the latest Spring Framework 4.0.1
- Added Java Configuration for web.xml, Spring MVC, Spring Security and MongoDB
- Added default services

# Overview

This repository represents a fully working implementation of a CloudFoundry MongoDB broker. This broker is completely untested regarding scaling. You might use any kind of MongoDB instance (single server, cluster etc.). Currently the implementation only supports one MongoDB Server. Depending the on the Cloud Environment it would be easily possible to spawn individual instances for each client. 

An sample implementation for Openstack is currently planned, but not yet done, due to less spare time. :-)

## Compatibility

- cf-release-151 on bosh-lite.
- Service Broker API: v2.1
- MongoDB 2.X.X

## Getting Started

To use:

1. Fork the project
2. Configure your MongoDB instance in `src/main/resources/application-mongodb.properties`
3. Build the project and run the tests: `mvn clean install`
4 Push the broker to CloudFoundry as an app: `cf push <your-broker> --path target/<war>`
5. Register your service broker with CF: `cf add-service-broker <service-broker-name>`
6. Add the URL
7. Get ready to use...

### Security

When you register your broker with the cloud controller, you are prompted to enter a username and password.  This is used by the broker to verify requests.

By default, the broker uses Spring Security to protect access to resources. The username and password are stored in: `/src/main/java/com/pivotal/cf/config/security/CustomSecurityConfiguration`. The password is not yet encrypted or stored in a database. For large infrastructure I recommend the usage of a Spring Security LDAP binding or other SSO implementations. If you have questions regarding that, feel free to contact me.

### Testing

Integration tests are included to test the controllers.  You are responsible for testing your service implementation.  

- Initial draft of RestTemplate endpoint tests.

### Model Notes

- The model is for the REST/Controller level.  It can be extended as needed.
- All models explicitly define serialization field names.

## To Do

* More integration testing around expected data input and output
* Version headers
* Integrate w/ NATS to allow this war to be deployed with Bosh
* Create a Bosh release
* Separate integration project to test broker endpoints


