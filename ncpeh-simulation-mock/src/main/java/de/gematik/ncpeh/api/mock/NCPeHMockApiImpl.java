/*
 * Copyright (Change Date see Readme), gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ******
 *
 * For additional notes and disclaimer from gematik and in case of changes
 * by gematik, find details in the "Readme" file.
 */

package de.gematik.ncpeh.api.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.ncpeh.api.NcpehSimulatorApi;
import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder;
import de.gematik.ncpeh.api.mock.builder.SimulatorCommunicationDataBuilder;
import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.api.mock.data.Patient;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.ProvideAndRegisterSetOfDocumentsRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * Implementation of the NCPeH Trigger Interface API It's all Spring and Apache CXF magic, so pretty
 * slim implementation
 */
@Slf4j
@Service
public class NCPeHMockApiImpl implements NcpehSimulatorApi {

  public static final String FILE_EXTENSIONS_XML = ".xml";
  public static final String HEADER_X_NCPEH_MOCK_RESPONSE = "X-NCPeHMock-Response";
  public static final String HEADER_X_NCPEH_MOCK_PATIENT = "X-NCPeHMock-Patient";
  public static final String HEADER_X_NCPEH_MOCK_MEDICATION = "X-NCPeHMock-Medication";
  public static final String OID_ASSIGNING_AUTHORITY_EPED = "1.2.276.0.76.4.299";
  public static final String XDS_DOCUMENT_ENTRY_CLASS_CODE_EPED =
      "('57833-6^^2.16.840.1.113883.6.1')";

  private final ObjectMapper mapper;

  @Context private HttpHeaders headers;

  public NCPeHMockApiImpl(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Response identifyPatient(final IdentifyPatientRequest request) {
    final var fileName = getFileNameFromRequestHeader();
    final var accessCode = getAccessCodeFromRequestHeader();

    if (request.accessCodeAssigningAuthority() != null
        && request.accessCodeAssigningAuthority().equals(OID_ASSIGNING_AUTHORITY_EPED)) {
      return okResponseBuilder()
          .entity(
              SimulatorCommunicationDataBuilder.newInstance()
                  .requestMessage(HttpMessageFactory.buildEPEDIdentifyPatientRequest())
                  .responseMessage(
                      HttpMessageFactory.buildStandardIdentifyPatientResponse(fileName, accessCode))
                  .build())
          .build();
    } else {
      return okResponseBuilder()
          .entity(
              SimulatorCommunicationDataBuilder.newInstance()
                  .requestMessage(HttpMessageFactory.buildPSAIdentifyPatientRequest())
                  .responseMessage(
                      HttpMessageFactory.buildStandardIdentifyPatientResponse(fileName, accessCode))
                  .build())
          .build();
    }
  }

  @Override
  public Response findDocuments(final FindDocumentsRequest request) {
    final var fileName = getFileNameFromRequestHeader();
    final var builder = SimulatorCommunicationDataBuilder.newInstance();
    final var medicationByPrescriptionId = getPrescribedMedicationsFromRequestHeader();
    final var prescriptionIds =
        medicationByPrescriptionId != null
            ? medicationByPrescriptionId.keySet()
            : Collections.<String>emptySet();

    if (XDS_DOCUMENT_ENTRY_CLASS_CODE_EPED.equals(request.xdsDocumentEntryClassCode())) {
      builder
          .requestMessage(HttpMessageFactory.buildEPEDFindDocumentRequest())
          .responseMessage(
              HttpMessageFactory.buildStandardFindDocumentResponseEPED(
                  fileName, prescriptionIds, medicationByPrescriptionId));
    } else {
      builder
          .requestMessage(HttpMessageFactory.buildPSAFindDocumentRequest())
          .responseMessage(HttpMessageFactory.buildStandardFindDocumentResponsePSA(fileName));
    }

    return okResponseBuilder().entity(builder.build()).build();
  }

  @Deprecated(since = "2.1.0.1", forRemoval = true)
  @Override
  public Response retrieveDocument(final RetrieveDocumentRequest request) {
    final var fileName = getFileNameFromRequestHeader();
    final var patient = getPatientFromRequestHeader();

    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(HttpMessageFactory.buildRetrieveDocumentRequest(request))
                .responseMessage(
                    HttpMessageFactory.buildRetrieveDocumentResponse(
                        RetrieveDocumentMessagesBuilder.buildFromRequestAndPatient(
                            request, patient),
                        fileName))
                .build())
        .build();
  }

  @Override
  public Response retrieveSetOfDocuments(final RetrieveSetOfDocumentsRequest request) {
    final var fileName = getFileNameFromRequestHeader();
    final var patient = getPatientFromRequestHeader();
    final var medicationByPrescriptionId = getPrescribedMedicationsFromRequestHeader();

    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(HttpMessageFactory.buildStandardRetrieveSetOfDocumentsRequest())
                .responseMessage(
                    HttpMessageFactory.buildRetrieveDocumentResponse(
                        RetrieveDocumentMessagesBuilder.fromRequest(request)
                            .patient(patient)
                            .medicationByPrescriptionId(medicationByPrescriptionId),
                        fileName))
                .build())
        .build();
  }

  @Override
  public Response provideAndRegisterSetOfDocuments(
      final ProvideAndRegisterSetOfDocumentsRequest request) {
    final var fileName = getFileNameFromRequestHeader();

    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(
                    HttpMessageFactory.buildStandardProvideAndRegisterSetOfDocumentsRequest())
                .responseMessage(
                    HttpMessageFactory.buildStandardProvideAndRegisterSetOfDocumentsResponse(
                        fileName))
                .build())
        .build();
  }

  private static ResponseBuilder okResponseBuilder() {
    return Response.ok().type(MediaType.APPLICATION_JSON_TYPE);
  }

  /**
   * Retrieves the file name for the mock response from the request header.
   *
   * @return the file name with XML extension, or null if the header is not present
   */
  private String getFileNameFromRequestHeader() {
    return Optional.ofNullable(headers.getHeaderString(HEADER_X_NCPEH_MOCK_RESPONSE))
        .map(name -> name + FILE_EXTENSIONS_XML)
        .orElse(null);
  }

  /**
   * Retrieves a Patient object from the request header.
   *
   * @return the Patient object, or null if the header is not present or deserialization fails
   */
  private Patient getPatientFromRequestHeader() {
    return readObjectFromHeader(HEADER_X_NCPEH_MOCK_PATIENT, new TypeReference<>() {});
  }

  /**
   * Retrieves the access code from the patient request header.
   *
   * @return the access code, or null if the patient header is not present or deserialization fails
   */
  private String getAccessCodeFromRequestHeader() {
    Patient patientFromHeader = getPatientFromRequestHeader();
    return patientFromHeader != null ? patientFromHeader.accessCode() : null;
  }

  /**
   * Retrieves a map of prescribed medications from the request header.
   *
   * @return a map where the key is the prescription ID and the value is the corresponding
   *     Medication object, or null if the header is not present or deserialization fails
   */
  private Map<String, Medication> getPrescribedMedicationsFromRequestHeader() {
    return readObjectFromHeader(HEADER_X_NCPEH_MOCK_MEDICATION, new TypeReference<>() {});
  }

  /**
   * Reads and decodes a Base64 encoded JSON object from the specified header and deserializes it
   * into an object of the specified type.
   *
   * @param headerName the name of the header to read from
   * @param typeRef the type reference for deserialization
   * @param <T> the type of the object to be returned
   * @return the deserialized object, or null if the header is not present or deserialization fails
   */
  @Nullable
  private <T> T readObjectFromHeader(final String headerName, final TypeReference<T> typeRef) {
    return Optional.ofNullable(headers.getHeaderString(headerName))
        .map(
            base64json -> {
              try {
                return mapper.readValue(Base64.getDecoder().decode(base64json), typeRef);
              } catch (final IOException e) {
                // nothing to do
                log.warn(
                    "Failed to parse Object of type %s from header".formatted(typeRef.getType()),
                    e);
              }
              return null;
            })
        .orElse(null);
  }
}
