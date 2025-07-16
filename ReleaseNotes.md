<img align="right" width="200" height="37" src="Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/>

# Release Notes NCPeH-Simulation-API

## Release 2.0.2 (2025-07-16)

### added

- Included additional POM metadata to meet Maven Central requirements

## Release 2.0.1 (2025-07-15)

### changed

- The PDF file returned when requesting a Patient Summary Document with format 'CDA level 1' is now
  generated dynamically based on patient data provided in the `X-NCPeHMock-Patient` request header
- Updated LICENSE
- Updated README

### fixed

- several fixes and small improvements

## Release 2.0.0 (2025-02-27)

### added

- New API endpoint `/retrieveSetOfDocuments` for triggering
  a [Cross Gateway Retrieve Request [ITI-39]](https://profiles.ihe.net/ITI/TF/Volume2/ITI-39.html)
- Included attribute `structuralRole` in `IdaAssertionProfile`

### fixed

- several vulnerability fixes and small improvements

## Release 1.6.3 (2025-01-08)

### changed

- **Breaking:** Attribute FindDocumentsRequest.xdsDocumentEntryClassCode is now mandatory
- Updated KVNRs for ePA 3.0
- Added SOAP envelope in mock responses
- Implemented responses for negative test cases

### fixed

- several vulnerability issues have been fixed

## Release 1.6.0 (2024-08-06)

### changed

- Attribute urn:oasis:names:tc:xspa:1.0:subject:functional-role removed
- Patient Identifier urn:oasis:names:tc:xacml:1.0:resource:resource-id renamed to urn:oasis:names:
  tc:
  xspa:1.0:subject:subject-id
- Attribute urn:oasis:names:tc:xacml:1.0:subject:subject-id rename to urn:oasis:names:tc:xspa:1.0:
  subject:subject-id

### fixed

- several vulnerability issues have been fixed

## Release 1.5.2 (2024-01-30)

### changed

- Default value of oid for KVNR was changed to 1.2.276.0.76.3.1.580.147, (previous ending to 047 is
  not permitted for oid)

### fixed

- Response templates AdhocQueryResponse.xml und PRPA_IN201306UV02.xml have been corrected. The given
  KVNR value missed the concatenation with the access code.

### known issues bzwg. CVE in SpringBoot

Currently this test suite uses the latest version of module cxf-spring-boot-starter-jaxrs (4.0.3),
whose imports contain following CVE's:
spring-aop-6.0.11.jar: CVE-2023-34053(7.5)
spring-boot-starter-json-3.0.10.jar: CVE-2023-34055(6.5)
spring-boot-starter-web-3.0.10.jar: CVE-2023-34055(6.5)
spring-core-6.0.14.jar: CVE-2024-22233(7.5)
spring-web-6.0.11.jar: CVE-2023-34053(7.5)

We will fix this problem in the next version.

## Release 1.5.0

### added

- Security Policy description in file SECURITY.MD

### changed

- Adapted Simulation-API to changes made in the 1.5 version of gemSpec_NCPeH_FD specification

### fixed

- Included newer versions of snakeyaml and tomcat-embed-websocket to avoid vulnerabilities in the
  versions shipped with Spring-Boot

## Release 1.0.3

### added

- Mock now supports CDA 1 & CDA 3 Patient Summary
- Third module ncpeh-simulation-ehdsi-model to generate Java classes for Patient Summary and
  Clinical Document from HL7v3 schema files

### changed

- For API definition switched from javax to jakarta
- Mock: When building the messages for the response of the retrieveDocument operation, it actually
  uses the data, which where provided in the request

## Release 1.0.2

### fixed

- Bug which prevented the ncpeh-simulation-mock from starting
- Default value for oidAssigningAuthority in the PatientId

### added

- additionalDocumentUniqueId in the retrieveDocument request, to be able to retrieve two documents
  with one request
- Readme files for the two modules of this project

### changed

- The mock now contains proper data in the httpBody elements of the responses,
  which are of correct type, containing plausible data for each operation

## Release 1.0.1

### fixed

- Updated version of the nexus-staging-maven-plugin, as the old version prevented deploy to Maven
  Central

## Release 1.0.0

### added

- added Java model of the API
- added OpenAPI description generated from the Java model
- added Deploy to Maven Central

