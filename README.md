cloudfoundry-service-broker
===========================
### This Repository is will be a central entrypoint to our new Repository structure. We are moving the code in this new repository structure. 
(Contains old repo and code structure for archiving for some time.)

Here are the links to the new repositories with a short discription:

---------------------------------

####Central Entrypoint to Repositories

evoila/cf-service-broker

https://github.com/evoila/cf-service-broker

Central entrypoint and links to sub-repositories. (Contains old repo and code structure for archiving for some time.)

---------------------------------------

####Example Service Broker

evoila/cf-service-broker-example

https://github.com/evoila/cf-service-broker-example

An empty Cloud Foundry Service Broker missing concrete implementation of a distinct service. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added. Concrete Service logic and binding logic has to be added.

-----------------------------------

####Infrastructure etc.

evoila/cf-service-broker-persistence

https://github.com/evoila/cf-service-broker-persistence

Implements the cf-service-broker (see https://github.com/evoila/cf-service-broker) persistence interface with services using Redis. So the management data and inner status of the service broker can be kept in a Redis database.


evoila/cf-service-broker-deployment

https://github.com/evoila/cf-service-broker-deployment

This project provides support for deployment of service instances via Cloud Foundry Service Broker (see https://github.com/evoila/cf-service-broker) as Docker Containers onto a Docker Swarm cluster or as Virtual Machines onto OpenStack via Heat Scripts


evoila/cf-service-broker-core

https://github.com/evoila/cf-service-broker-core

This project provides core functionalities for Cloud Foundry Service Broker. It needs deployment services and a persistence service (see https://github.com/evoila/cf-service-broker-deployment and https://github.com/evoila/cf-service-broker-persistence) as well as a concrete implementation of service binding routines for a concrete service (for examples see https://github.com/evoila/cf-service-broker-example and https://github.com/evoila/cf-service-broker-elasticsearch).


evoila/cf-service-broker-infrastructure

https://github.com/evoila/cf-service-broker-infrastructure

This project provides infrastructure services (like cloud configuration) for Cloud Foundry Service Broker (see https://github.com/evoila/cf-service-broker)

---------------------------------

####Documentation

evoila/cf-service-broker-doc

https://github.com/evoila/cf-service-broker-doc

Links to the documentation.

-------------------------------

####Services

evoila/cf-service-broker-elasticsearch

https://github.com/evoila/cf-service-broker-redis

Cloud Foundry Service Broker providing Elasticsearch Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.


evoila/cf-service-broker-redis

https://github.com/evoila/cf-service-broker-redis

Cloud Foundry Service Broker providing Redis Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.

evoila/cf-service-broker-logstash

https://github.com/evoila/cf-service-broker-logstash

Cloud Foundry Service Broker providing Logstash Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.

evoila/cf-service-broker-mongodb

https://github.com/evoila/cf-service-broker-mongodb

Cloud Foundry Service Broker providing MongoDB Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.

evoila/cf-service-broker-mysql

https://github.com/evoila/cf-service-broker-mysql

Cloud Foundry Service Broker providing MySQL (MariaDB) Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.

evoila/cf-service-broker-postgresql

https://github.com/evoila/cf-service-broker-postgresql

Cloud Foundry Service Broker providing PostgreSQL Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.

evoila/cf-service-broker-rabbitmq

https://github.com/evoila/cf-service-broker-rabbitmq

Cloud Foundry Service Broker providing RabbitMQ Service Instances. Supports deployment to OpenStack and Docker. Uses Redis Database for management. Configuration files and deployment scripts must be added.

------------------------

The current version of the documentation is under construction and can be found at https://github.com/evoila/cf-service-broker/blob/master/docs/index.adoc
[//]: #  ( ### Security )

[//]: #  ( When you register your broker with the cloud controller, you are prompted to enter a username and password.  This is used by the broker to verify requests. )

[//]: #  ( By default, the broker uses Spring Security to protect access to resources. The username and password are stored in: `/src/main/java/com/pivotal/cf/config/security/CustomSecurityConfiguration`. The password is not yet encrypted or stored in a database. For large infrastructure I recommend the usage of a Spring Security LDAP binding or other SSO implementations. If you have questions regarding that, feel free to contact me. )

[//]: #  ( ### Testing )

[//]: #  ( Integration tests are included to test the controllers.  You are responsible for testing your service implementation.  )

[//]: #  ( - Initial draft of RestTemplate endpoint tests. )

[//]: #  ( ### Model Notes )

[//]: #  ( - The model is for the REST/Controller level.  It can be extended as needed. )
[//]: #  ( - All models explicitly define serialization field names. )

[//]: #  ( ## To Do )

[//]: #  ( * More integration testing around expected data input and output )
[//]: #  ( * Version headers )
[//]: #  ( * Integrate w/ NATS to allow this war to be deployed with Bosh )
[//]: #  ( * Create a Bosh release )
[//]: #  ( * Separate integration project to test broker endpoints )

