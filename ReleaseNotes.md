<img align="right" width="200" height="37" src="Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/>

# Release notes NCPeH-Simulation-API

## Release 1.5.0

### added
- Security Policy description in file SECURITY.MD

### changed
- Adapted Simulation-API to changes made in the 1.5 version of gemSpec_NCPeH_FD specification

### fixed
- Included newer versions of snakeyaml and tomcat-embed-websocket to avoid vulnerabilities in the versions shipped with Spring-Boot


## Release 1.0.3

### added
- Mock now supports CDA 1 & CDA 3 Patient Summary
- Third module ncpeh-simulation-ehdsi-model to generate Java classes for Patient Summary and Clinical Document from HL7v3 schema files

### changed
- For API definition switched from javax to jakarta
- Mock: When building the messages for the response of the retrieveDocument operation, it actually uses the data, which where provided in the request


## Release 1.0.2

### fixed
- Bug which prevented the ncpeh-simulation-mock from starting
- Default value for oidAssigningAuthority in the PatientId

### added
- additionalDocumentUniqueId in the retrieveDocument request, to be able to retrieve two documents with one request
- Readme files for the two modules of this project

### changed
- The mock now contains proper data in the httpBody elements of the responses,
  which are of correct type, containing plausible data for each operation


## Release 1.0.1

### fixed
- Updated version of the nexus-staging-maven-plugin, as the old version prevented deploy to Maven Central


## Release 1.0.0

### added
- added Java model of the API
- added OpenAPI description generated from the Java model
- added Deploy to Maven Central

