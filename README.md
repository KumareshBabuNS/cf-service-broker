= Cloudfoundry Service-Broker

. link:README.adoc[Getting Started]
. link:docs/requirements.adoc[Requirements]
. Installation
.. link:docs/setup.adoc[Setup]
.. link:docs/deploymentscripts.adoc[Installation & Configuration Scripts]
. link:docs/usage.adoc[Usage]
. link:docs/repositories.adoc[Repositories]
. link:docs/developers.adoc[Developers]
. link:docs/license.adoc[License]

== 1. Getting Started

*Foreword*: We decided to seperate the documentations about installation, usage etc. and not to put them all in the README.md-file in order to not have one overfilled file.

=== What are Service Brokers?

A service Broker provides the possibility to extend a platform (in our case it's Cloud Foundry) with services (for example a database) that can be consumed by applications deployed to this platform.

image::docs/assets/service_broker_1.png[Service Broker]

It comes with a catalog of services and service plans, provides service instances, and contains connection details and credentials.

image::docs/assets/service_broker_2.png[Service Broker]

For a better understanding of Cloud Foundry Service Brokers also visit https://docs.cloudfoundry.org/services/api.html.

=== Why use Service Broker?