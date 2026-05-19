<img align="right" width="200" height="37" src="../Gematik_Logo_Flag.png" alt="Gematik Logo"/> <br/> 

# NCPeH-Simulation-Mock

This module provides a simple mock implementation of the NCPeH-Simulation-API, offering
developers a test environment for their API clients.

The mock does not verify or validate requests. The responses it returns are technically correct
but are generated from templates and do not reflect the data submitted in the request.

> [!IMPORTANT]
> The SOAP messages returned by the mock currently do not include the headers required for the
> individual transactions.

Since this is a Spring Boot application, it can be started with:

    java -jar ncpeh-simulation-mock-<version>.jar

or via the Maven plugin:

    mvn spring-boot:run

The default URL for calling API operations is:
`http://<hostname or IP>:8082/rest/triggerInterface/<operation name>`

The OpenAPI UI is available at `http://<hostname or IP>:8082/rest/api-docs?url=/rest/openapi.json`

Pre-built artifacts are also available
on [Maven Central](https://repo1.maven.org/maven2/de/gematik/api/ncpeh-simulation-mock/).

## Controlling Mock Behaviour via HTTP Headers

The NCPeH-Simulation-Mock supports custom HTTP headers to control response behaviour for testing
different scenarios.

### Use Cases and Headers

[Controlling Response Content](#controlling-response-content)

- `X-NCPeHMock-Response`

[Providing Data for Dynamic Document Generation](#providing-data-for-dynamic-document-generation)

- `X-NCPeHMock-Patient`
- `X-NCPeHMock-Medication`

### Controlling Response Content

#### `X-NCPeHMock-Response`

**Type:** String (predefined values)  
**Purpose:** Controls the HTTP response content returned by endpoints

**Supported Values by Endpoint:**

**/identifyPatient**

- Default: Returns one patient matching query criteria
- `PRPA_IN201306UV02_4`: No patients matching criteria
- `PRPA_IN201306UV02_41`: No patients, defective identity information in Germany
- `PRPA_IN201306UV02_42`: No patients, incomplete health record (missing date of birth)
- `PRPA_IN201306UV02_43`: No patients, incomplete/defective identity information (ePKA document with
  DPE Composition only)
- `PRPA_IN201306UV02_44`: No patients, unknown KVNR
- `PRPA_IN201306UV02_45`: No patients, wrong AccessCode
- `PRPA_IN201306UV02_5`: Application error

**/findDocuments**

- Default: AdhocQueryResponse with `ResponseStatusType: Success`
- `AdhocQueryResponse_010`: Failure response (valid ePKA with single DPE composition)
- `AdhocQueryResponse_031`: Failure response (missing EU member state authorisation)

**/retrieveDocument**

- `RetrieveDocumentSetResponse_020`: Failure response (valid ePKA with new DocumentUniqueId)
- `RetrieveDocumentSetResponse_032`: Failure response (missing EU member state authorisation)

### Providing Data for Dynamic Document Generation

The following headers are used to provide data to be included in the generated documents returned
by the **/retrieveDocument** and **/retrieveSetOfDocuments** endpoints.

#### `X-NCPeHMock-Patient`

**Supported by Endpoints:** `/identifyPatient`, `/retrieveDocument` and `/retrieveSetOfDocuments`
**Type:** Base64-encoded JSON representation of
a [Patient](src/main/java/de/gematik/ncpeh/api/mock/data/Patient.java)  
**Purpose:** Provides patient data for inclusion in the following dynamically generated documents:

- Patient Summary (CDA 1)
- eHDSI ePrescription (CDA 1)
- eHDSI ePrescription (CDA 3)

If the JSON also contains an `accessCode`, the mock uses it in the successful
`/identifyPatient` response instead of the default template value.

#### `X-NCPeHMock-Medication`

**Supported by Endpoints:** `/retrieveSetOfDocuments`, `/findDocuments`
**Type:** Base64-encoded JSON mapping of prescription IDs
to [Medication](src/main/java/de/gematik/ncpeh/api/mock/data/Medication.java) data  
**Purpose:** Provides medication data for inclusion in eHDSI ePrescription documents and controls which prescription
  document entries are generated in the **/findDocuments** response.

For each provided prescription ID, the mock generates **two document entries**:

- one CDA 1 document (`eP.PDF`)
- one CDA 3 document (`eP.XML`)

Each generated document entry has an ID in the format:

```
<OID_ASSIGNING_AUTHORITY_EPED>^<prescriptionId>|eP.XML
<OID_ASSIGNING_AUTHORITY_EPED>^<prescriptionId>|eP.PDF
```

The template document entries contained in the response are replaced by the
newly generated entries.

If the header is omitted, the default template response is returned unchanged.
