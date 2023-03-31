<img align="right" width="200" height="37" src="Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/> 

# NCPeH Simulation API

## About the Project
API specification for the testdriver interface of an NCPeH simulation. 
The purpose of the NCPeH simulation is to enable the testing of the german NCPeH service. 
The purpose of the API is to provide a defined interface for testsuites to trigger actions at the simulation 
and receive the contents of the resulting communication between simulation and NCPeH service.

The API is defined in Java, but from it a description in the OpenAPI format is generated and available via Maven Central.

A more detailed description is following in the chapter [Fachliche Einordnung](#fachliche-einordnung),
but as the intended audience is part of the german eHealth project, it is in german.

### Release-Notes
The Release-Notes can be found in the file [ReleaseNotes.md](./ReleaseNotes.md)

## Modules
The NCPeH Simulation API project consists of two modules:
1. ncpeh-simulation-td-api: Contains the actual API definition, see [NCPeH-Simulation-TD-API README](./ncpeh-simulation-td-api/README.md)
2. ncpeh-simulation-mock: A lightweight mock to illustrate implementation of the API, see [NCPeH-Simulation-Mock README](./ncpeh-simulation-mock/README.md)

## Getting started
Clone the project, if you want to, but this is probably not necessary if you just want to integrate the API in your project.
The easiest way for a maven project is to simply add the dependency:<br/>

    <dependency>
      <groupId>de.gematik.api</groupId>
      <artifactId>ncpeh-simulation-td-api</artifactId>
      <version>x.y.z</version>
    </dependency>  

For gradle it is

    implementation 'de.gematik.api:ncpeh-simulation-td-api:x.y.z'

### Prerequisites
In case you want to clone the project you need:
* Git
* Java JDK 16 or newer (Tested on OpenJDK)
* Maven 3.8.0 or newer

For non Java-Projects code can be generated from the OpenAPI description available at Maven Central.

## License
See [LICENSE](./LICENSE)

## Contributing
Right now this is not a collaborative project, so input can only be given through the
[issue management system](https://github.com/gematik/NCPeH-Simulation-API/issues).


## Fachliche Einordnung
Die Szenarien des europäischen Datentransfers, wie z. B. das in Deutschland zuerst umzusetzende "Patient Summary Country
A" erfordert neben dem NCPeH-Fachdienst die Beteiligung verschiedener Produkte der TI (Produktkette), wie z. B. ePA
Aktensysteme, Konnektoren etc.
Um sowohl Herstellern von Produkten der TI als auch der gematik eine eigenständige und automatisierte Durchführung von
integrativen Tests in den Umgebungen RU und TU zu ermöglichen wird diese NCPeH Simulation API zum Auslösen von
Anwendungsfällen in beiden Umgebungen bereitstellen (NCPeH-Testinterface). Das NCPeH-Testinterface soll dabei die eHDSI
Nachrichten generieren, an den NCPeH-FD senden und dessen Antwort entgegen nehmen.
Das NCPeH Testinterface gibt als Response auf die REST-Requests die Inhalte der Nachrichten vom eHDSI-Interface zurück.
Also den jeweiligen IHE-Request und die IHE-Responses, die mit dem NCPeH-Fachdienst ausgetauscht wurden. Beides wird
jeweils aufgeteilt in den http-Header und den http-Body, jeweils Base64-kodiert. Damit soll dem aufrufenden Testfall die
Möglichkeit gegeben werden, die Reaktion des NCPeH-Fachdienstes direkt zu prüfen und zu bewerten.
Der Zugang zur NCPeH-Testinterface soll mittels Gateway über das Internet ermöglicht und per TLS abgesichert werden.

### Daten für das NCPeH-Testinterface

Das NCPeH-Testinterface nutzt für einige Eingabeparameter Objekte, die an zu definierende Datenprofile gekoppelt sind (
EuCountryCode, IdAAssertionProfile/ProfileName, TRCAssertionProfile/ProfileName).

Ein Datenprofil fungiert einerseits wie ein Template, in dem die Daten hinterlegt sind, die für die Kommunikation über
die eHDSI Schnittstelle verwendet werden sollen, so dass diese nicht alle über die Triggerschnittstelle bereitgestellt
werden müssen. Damit soll auch die Komplexität der Schnittstelle reduziert werden. Andererseits definiert es auch Daten,
die in der Kommunikation mit dem NCPeH-FD eingehen. Am Wichtigsten ist hier die Referenz auf Zertifikate, die zu nutzen
sind.

Datenprofile, die von der gematik oder Herstellern beteiligter TI-Produkte benötigt werden, müssen im Rahmen der
Bereitstellung der Schnittstelle zwischen dem Betreiber des NCPeH-Testinterface und der gematik abgestimmt werden. Die
gemeinsam definierten Datenprofile sind dementsprechend mit dem NCPeH-Testinterface zur Nutzung bereitzustellen.

Das Objekt 'EuCountryCode' referenziert folgendes Datenprofil:

* Das TLS-Zertifikat, das für die Kommunikation mit dem NCPeH-FD genutzt werden soll
* Die HomeCommunityId, die in dem Profil per Default für den NCPeH Land B genutzt werden soll

Das Objekt 'IdAAssertionProfile' referenziert folgendes Datenprofil:

* Das Signatur-Zertifikat, das zum Signieren der IdA-Assertion zum Einsatz kommen soll
* Der Wert, der im SAML-Element Assertion/Subject/NameID einzutragen ist (Identifier des LE-EU) als auch das Format des
  Wertes (Attribut @Format)
* Die Werte, die in dem Profil per Default in den Attributen der Struktur AttributeStatement einzutragen oder
  wegzulassen sind:
    - urn:oasis:names:tc:xacml:1.0:subject:subject-id
    - urn:oasis:names:tc:xacml:2.0:subject:role
    - urn:oasis:names:tc:xspa:1.0:subject:functional-role
    - urn:ehdsi:names:subject:clinical-speciality
    - urn:ehdsi:names:subject:on-behalf-of
    - urn:oasis:names:tc:xspa:1.0:subject:organization
    - urn:oasis:names:tc:xspa:1.0:subject:organization-id
    - urn:ehdsi:names:subject:healthcare-facility-type
    - urn:oasis:names:tc:xspa:1.0:environment:locality
    - urn:oasis:names:tc:xspa:1.0:subject:hl7:permission

Das Objekt 'TRCAssertionProfile' referenziert folgendes Datenprofil:

* Das Signatur-Zertifikat, das zum Signieren der TRC-Assertion zum Einsatz kommen soll
* Der Wert, der im SAML-Element Assertion/Subject/NameID einzutragen ist (Identifier des LE-EU) als auch das Format des
  Wertes (Attribut @Format)
