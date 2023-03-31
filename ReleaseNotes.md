<img align="right" width="200" height="37" src="Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/>

# Release notes NCPeH-Simulation-API

## Release 1.0.2

### fixed
- Bug which prevented the ncpeh-simualtion-mock from starting
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

