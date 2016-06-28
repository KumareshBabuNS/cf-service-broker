cloudfoundry-service-broker for different backends like PostgreSQL, MongoDB
===========================
### Documentation is currently under work and will be extended in future
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

