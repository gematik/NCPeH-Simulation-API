<img align="right" width="250" height="47" src="Gematik_Logo_Flag_With_Background.png" alt="Gematik Logo"/> <br/> 

# NCPeH Simulation API

## About the Project

This project defines the API specification for the test driver interface of an NCPeH simulation.
The purpose of the NCPeH simulation is to enable testing of the German NCPeH service.
The purpose of the API is to provide a defined interface for testsuites to trigger actions in the
simulation and receive the contents of the resulting communication between simulation and NCPeH
service.

The API is defined in Java. An OpenAPI Description is generated from these sources and made
available on Maven Central.

A more detailed description is given in the chapter [Fachliche Einordnung](#fachliche-einordnung).
As the intended audience is part of the German eHealth project, it is written in German.

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

Clone the project if you want to, but this is probably not necessary if you just want to integrate
the API in your project.

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

In case you want to clone the project, you need:

* Git
* Java JDK 21 or newer (Tested on OpenJDK)
* Maven 3.8.0 or newer

For non-Java projects, code can be generated from the OpenAPI description available on Maven
Central.

## License

Copyright 2022-2025 gematik GmbH

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
4. Please note: Parts of this code may have been generated using AI-supported technology. Please
   take this into account, especially when troubleshooting, for security analyses and possible
   adjustments.

## Contributing

Right now this is not a collaborative project, so input can only be given through the
[issue management system](https://github.com/gematik/NCPeH-Simulation-API/issues).

## Fachliche Einordnung

Die Szenarien des europäischen Datentransfers, wie z. B. das in Deutschland zuerst umzusetzende
"Patient Summary Country A" erfordert neben dem NCPeH-Fachdienst die Beteiligung verschiedener
Produkte der TI (Produktkette), wie z. B. ePA Aktensysteme, Konnektoren etc.
Um sowohl Herstellern von Produkten der TI als auch der gematik eine eigenständige und
automatisierte Durchführung von
integrativen Tests in den Umgebungen RU und TU zu ermöglichen, wird diese NCPeH Simulation API zum
Auslösen von
Anwendungsfällen in beiden Umgebungen bereitgestellt (NCPeH-Testinterface).  
Über das NCPeH-Testinterface sollen von Testszenarien eHDSI-Anwendungsfälle angestoßen werden.
Dabei sollen IHE-Nachrichten von einem NCPeH-Simulator generiert, an den NCPeH-FD gesendet und die
Antwort vom NCPeH-FD entgegengenommen werden. Der generierte Request und die vom NCPeH empfangene
Antwort
werden über das NCPeH-Testinterface an den aufrufenden Testfall zurückgegeben.
Also das jeweilige IHE-Request- und IHE-Response-Paar, das mit dem NCPeH-Fachdienst ausgetauscht
wurde. Beide werden aufgeteilt in

* die Kopfzeile des HTTP Requests, mit der HTTP Methode und der Zieladresse  
  bzw.
* die Statuszeile der HTTP Antwort, mit dem Status Code und der Status Zusammenfassung
* den http-Header und
* den http-Body,

jeweils Base64-kodiert. Damit soll dem aufrufenden Testfall die
Möglichkeit gegeben werden, die Reaktion des NCPeH-Fachdienstes direkt zu prüfen und zu bewerten.
Der Zugang zum NCPeH-Testinterface soll mittels Gateway über das Internet ermöglicht und per TLS
abgesichert werden.

### Daten für das NCPeH-Testinterface

Das NCPeH-Testinterface nutzt für einige Eingabeparameter Objekte, die an zu definierende
Datenprofile gekoppelt sind (
EuCountryCode, IdAAssertionProfile/ProfileName, TRCAssertionProfile/ProfileName).

Ein Datenprofil fungiert einerseits wie ein Template, in dem die Daten hinterlegt sind, die für die
Kommunikation über
die eHDSI Schnittstelle verwendet werden sollen, sodass diese nicht alle über die
Triggerschnittstelle bereitgestellt
werden müssen. Damit soll auch die Komplexität der Schnittstelle reduziert werden. Andererseits
definiert es auch Daten,
die in der Kommunikation mit dem NCPeH-FD eingehen. Am Wichtigsten ist hier die Referenz auf
Zertifikate, die zu nutzen
sind.

Datenprofile, die von der gematik oder Herstellern beteiligter TI-Produkte benötigt werden, müssen
im Rahmen der
Bereitstellung der Schnittstelle zwischen dem Betreiber des NCPeH-Testinterface und der gematik
abgestimmt werden. Die
gemeinsam definierten Datenprofile sind dementsprechend mit dem NCPeH-Testinterface zur Nutzung
bereitzustellen.

Das Objekt 'EuCountryCode' referenziert folgendes Datenprofil:

* Das TLS-Zertifikat, das für die Kommunikation mit dem NCPeH-FD genutzt werden soll
* Die HomeCommunityId, die in dem Profil per Default für den NCPeH Land B genutzt werden soll

Das Objekt 'IdAAssertionProfile' referenziert folgendes Datenprofil:

* Das Signatur-Zertifikat, das zum Signieren der IdA-Assertion zum Einsatz kommen soll
* Der Wert, der im SAML-Element Assertion/Subject/NameID einzutragen ist (Identifier des LE-EU) als
  auch das Format des Wertes (Attribut @Format)
* Die Werte, die in dem Profil per Default in den Attributen der Struktur AttributeStatement
  einzutragen oder wegzulassen sind:
    - urn:oasis:names:tc:xspa:1.0:subject:subject-id
    - urn:oasis:names:tc:xacml:2.0:subject:role
    - urn:ehdsi:names:subject:clinical-speciality
    - urn:ehdsi:names:subject:on-behalf-of
    - urn:oasis:names:tc:xspa:1.0:subject:organization
    - urn:oasis:names:tc:xspa:1.0:subject:organization-id
    - urn:ehdsi:names:subject:healthcare-facility-type
    - urn:oasis:names:tc:xspa:1.0:environment:locality
    - urn:oasis:names:tc:xspa:1.0:subject:hl7:permission

Das Objekt 'TRCAssertionProfile' referenziert folgendes Datenprofil:

* Das Signatur-Zertifikat, das zum Signieren der TRC-Assertion zum Einsatz kommen soll
* Der Wert, der im SAML-Element Assertion/Subject/NameID einzutragen ist (Identifier des LE-EU) als
  auch das Format des
  Wertes (Attribut @Format)
