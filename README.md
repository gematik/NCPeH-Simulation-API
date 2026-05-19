<img align="right" width="250" height="47" src="Gematik_Logo_Flag_With_Background.png" alt="Gematik Logo"/> <br/> 

# NCPeH Simulation API

## About the Project

This project defines the API specification for the test driver interface of an NCPeH simulation.
The NCPeH simulation enables testing of the German NCPeH service within a national test
environment. The API provides a well-defined interface through which test suites can trigger
actions in the simulation and inspect the resulting communication between the simulation and
the NCPeH service.

The API is defined in Java. An OpenAPI description is generated from these sources and published
on [Maven Central](https://repo1.maven.org/maven2/de/gematik/api/ncpeh-simulation-td-api/).

### Technical context

The **National Contact Point for eHealth (NCPeH)** is the gateway that connects each EU Member
State's national digital health infrastructure to the cross-border **eHealth Digital Service
Infrastructure (eHDSI)**. When a patient from one country (the *country of affiliation*,
"country A") seeks treatment in another country (the *country of treatment*, "country B"),
the two countries' NCPeH services communicate via IHE-based transactions to exchange the
required health data.

Germany, acting as **country A**, exposes an EU-facing interface on its NCPeH service. Through
this interface, an NCPeH country B can request data held in German digital health services.  
There are currently two eHDSI use cases being implemented:

* **Patient Summary (PS-A):** Retrieves a document containing essential health information
  about a German patient receiving treatment abroad in another European country.
* **ePrescription / eDispensation (eP/eD-A):** Retrieves prescriptions for a German patient
  abroad and sends dispensation information back to the German e-prescription service.

To support these features, the NCPeH needs to interact with the relevant services of the German
Telematics Infrastructure (TI): Patient Summary data is sourced from the German electronic health
record (*elektronische Patientenakte*, ePA), whereas prescription and dispensation data is exchanged
with the German e-prescription service (*E-Rezept-Fachdienst*).

**Why a country B simulator?**

Integration and interoperability testing of the German NCPeH service requires an entity that
can act as a country B peer on the EU-facing IHE interface. However, the test parties (i.e., gematik
and the providers of TI services) typically cannot access this interface directly, nor do
they necessarily have detailed knowledge of the IHE-based protocol.  
The **NCPeH country B simulator** fills this gap: it communicates directly with the German NCPeH
service via its IHE interface and exposes a simplified, REST-based **test driver interface** that
test suites can call instead.

### Functionality of the country B simulator

Calling a REST operation on the test driver interface triggers the transmission of the associated
IHE transaction to the NCPeH, emulating the actions of the Country B peer. A test case sends a
request to the simulator containing the information needed to construct the IHE message:

* The IHE operation to perform
* Data for the SAML assertion identifying the health professional and their organisation
  in country B
* Data for the SAML assertion describing the treatment context between the health professional
  and the patient
* Patient identifiers and access codes for the relevant German data service
* Document metadata (for search or transfer operations)

After the simulator constructs and sends the IHE request to the German NCPeH service and
receives a response, it returns the full exchange to the test case as base64-encoded data:

* **HTTP request** sent to the German NCPeH service
    * Request method and URL
    * HTTP headers
    * HTTP body (SOAP-based IHE operation request)
* **HTTP response** received from the German NCPeH service
    * Status line (status code and reason phrase)
    * HTTP headers
    * HTTP body (SOAP-based IHE operation response)

Access to the test interface of the Country B simulator is to be provided via the internet and
secured using TLS. A separate simulation instance must be provided for each of the German test
environments RU and TU (see
[gemKpt_Test, chapter 6 "Systemumgebungen"](https://gemspec.gematik.de/docs/gemKPT/gemKPT_Test/latest/#6)).

A description of the test data used by the test driver interface can be found in
[test-data-description.md](./doc/test-data-description.md).

### Versioning Scheme

Starting with version 2.1.0.0, the NCPeH-Simulation-API uses a four-digit version number:

- The first three digits mirror the referenced German NCPeH specification version.
- The fourth digit reflects additional updates or changes to the API itself.

This change is intended to facilitate clear identification of the referenced specification version
for each API release.

### Release Notes

The Release Notes can be found in the file [ReleaseNotes.md](./ReleaseNotes.md)

## Modules

The NCPeH Simulation API project consists of three modules:

1. ncpeh-simulation-td-api: Contains the actual API definition,
   see [NCPeH-Simulation-TD-API README](./ncpeh-simulation-td-api/README.md)
2. ncpeh-simulation-mock: A lightweight mock to illustrate implementation of the API,
   see [NCPeH-Simulation-Mock README](./ncpeh-simulation-mock/README.md)
3. ncpeh-simulation-ehdsi-model: Contains the data models and structures required for communication
   and data exchange in the context of the NCPeH simulation and the eHDSI interface.

## Getting Started

If you want to use this API in your own project, the standard approach is to add it as a Maven
dependency. In that case, cloning this repository is usually not necessary.

### Using the API in Your Project

The easiest way for a Maven project is to simply add the dependency:

    <dependency>
      <groupId>de.gematik.api</groupId>
      <artifactId>ncpeh-simulation-td-api</artifactId>
      <version>x.y.z</version>
    </dependency>  

For Gradle, it is:

    implementation 'de.gematik.api:ncpeh-simulation-td-api:x.y.z'

### Cloning This Project

If you do want to clone the project, you will need:

* Git
* Java JDK 21 or newer (Tested on OpenJDK)
* Maven 3.8.0 or newer

For non-Java projects, you can generate code from the OpenAPI description available on Maven
Central.

## License

Copyright 2022-2026 gematik GmbH

Apache License, Version 2.0

See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under
the License

## Additional Notes and Disclaimer from gematik GmbH

1. Copyright notice: Each published work result is accompanied by an explicit statement of the
   license conditions for use. These are regularly typical conditions in connection with open source
   or free software. Programs described/provided/linked here are free software, unless otherwise
   stated.
2. Permission notice: Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal in the Software
   without restriction, including without limitation the rights to use, copy, modify, merge,
   publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to
   whom the Software is furnished to do so, subject to the following conditions:
    1. The copyright notice (Item 1) and the permission notice (Item 2) shall be included in all
       copies or substantial portions of the Software.
    2. The software is provided "as is" without warranty of any kind, either express or implied,
       including, but not limited to, the warranties of fitness for a particular purpose,
       merchantability, and/or non-infringement. The authors or copyright holders shall not be
       liable in any manner whatsoever for any damages or other claims arising from, out of or in
       connection with the software or the use or other dealings with the software, whether in an
       action of contract, tort, or otherwise.
    3. The software is the result of research and development activities, therefore not necessarily
       quality assured and without the character of a liable product. For this reason, gematik does
       not provide any support or other user assistance (unless otherwise stated in individual cases
       and without justification of a legal obligation). Furthermore, there is no claim to further
       development and adaptation of the results to a more current state of the art.
3. Gematik may remove published results temporarily or permanently from the place of publication at
   any time without prior notice or justification.
4. Parts of this software and - in isolated cases - content such as text or images may have been
   developed using the support of AI tools. They are subject to the same reviews, tests, and
   security checks as any other contribution. The functionality of the software itself is not based
   on AI decisions.

## Contributing

Right now this is not a collaborative project, so feedback can only be provided through the
[issue tracker](https://github.com/gematik/NCPeH-Simulation-API/issues).
