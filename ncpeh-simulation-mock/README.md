<img align="right" width="200" height="37" src="../Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/> 

# NCPeH-Simulation-Mock

This module provides a simple mock implementation of the NCPeH-Simulation-API to offer
developers a test environment for their API clients.

The mock does not verify or validate requests and the responses it returns are technically correct,
but they are created from templates and do not reflect the data submitted in the request.

> [!IMPORTANT]
> The SOAP messages returned by the mock currently do not include the headers required for the
> individual transactions.

As it is a Spring Boot application, it can simply be started using the command:

    java -jar ncpeh-simulation-mock-<version>.jar

or maven plugin:

    mvn spring-boot:run

The URL to call the API operations defaults to:
`http://<hostname or IP>:8082/rest/triggerInterface/<operation name>`

The OpenApi-UI is available on `http://<hostname or IP>:8082/rest/api-docs?url=/rest/openapi.json`

The built versions can also be found
on [Maven Central](https://repo1.maven.org/maven2/de/gematik/api/ncpeh-simulation-mock/).

# Control NCPeH-Simulation-Mock responses

Use **HTTP** request header `X-NCPeHMock-Response` to control the HTTP response **content** of the
identifyPatient(IdentifyPatientRequest request).

Per default response body has exactly one patient record matching the criteria sent in the query
parameters.
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_4` to return response body with no patients
anywhere close to matching the criteria sent in the query parameters.
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_41` to return response body with no patients
while the found patient identity information in Germany is defective.
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_42` to return response body with no patients
while the found patient's health record data ist not complete, the date of birth is missing.
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_43` to return response body with no patients
while the patient identity information in Germany is incomplete or defective. (Request for account
with ePKA document only with DPE Composition)
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_44` to return response body with no patients
anywhere close to matching the criteria sent in the query parameters (KVNR is unknown).
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_45` to return response body with no patients
because wrong AccessCode in the query parameters.
Set header `X-NCPeHMock-Response` to `PRPA_IN201306UV02_5` to return response body with application
error.

Use **HTTP** request header `X-NCPeHMock-Response` to control the HTTP response **content** of the
findDocuments(final FindDocumentsRequest request).

Per default response body has AdhocQueryResponse with ResponseStatusType:Success.
Set header `X-NCPeHMock-Response` to `AdhocQueryResponse_010` to return response body with
AdhocQueryResponse with ResponseStatusType:Failure. (The account contains a valid ePKA document that
only contains one DPE composition)
Set header `X-NCPeHMock-Response` to `AdhocQueryResponse_031` to return response body with
AdhocQueryResponse with ResponseStatusType:Failure. (Authorization for EU member state is missing)

Use **HTTP** request header `X-NCPeHMock-Response` to control the HTTP response **content** of the
retrieveDocument(final RetrieveDocumentRequest request).

Set header `X-NCPeHMock-Response` to `RetrieveDocumentSetResponse_020` to return response body with
RegistryResponse_020 with ResponseStatusType:Failure. (The account contains a valid ePKA document
with new DocumentUniqueId)
Set header `X-NCPeHMock-Response` to `RetrieveDocumentSetResponse_032` to return response body with
RegistryResponse_032 with ResponseStatusType:Failure. (Authorization for EU member state is missing)

Use **HTTP** request header `X-NCPeHMock-Patient` to provide patient data which is to be used when
generating the Patient Summary document as CDA level 1 for the retrieveDocument operation.  
The header value must be a base64-encoded JSON serialization of
a [Patient](src/main/java/de/gematik/ncpeh/api/mock/data/Patient.java) object.
