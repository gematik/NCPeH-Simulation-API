/*
 * Copyright 2024-2025 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 */

package de.gematik.ncpeh.api.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.ncpeh.api.NcpehSimulatorApi;
import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder;
import de.gematik.ncpeh.api.mock.builder.SimulatorCommunicationDataBuilder;
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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
  @Context private HttpHeaders headers;

  public static final String oidAssigningAuthority_psa = "1.2.276.0.76.4.298";
  public static final String oidAssigningAuthority_eped = "1.2.276.0.76.4.299";

  public static final String XDS_DOCUMENT_ENTRY_CLASS_CODE_EPED =
      "('57833-6^^2.16.840.1.113883.6.1')";

  private final ObjectMapper mapper;

  public NCPeHMockApiImpl(final ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public Response identifyPatient(final IdentifyPatientRequest request) {
    final var fileName = getFileNameFromRequestHeader();

    if (request.accessCodeAssigningAuthority() != null
        && request.accessCodeAssigningAuthority().equals(oidAssigningAuthority_eped)) {
      return okResponseBuilder()
          .entity(
              SimulatorCommunicationDataBuilder.newInstance()
                  .requestMessage(HttpMessageFactory.buildEPEDIdentifyPatientRequest())
                  .responseMessage(
                      HttpMessageFactory.buildStandardIdentifyPatientResponse(fileName))
                  .build())
          .build();
    } else {
      return okResponseBuilder()
          .entity(
              SimulatorCommunicationDataBuilder.newInstance()
                  .requestMessage(HttpMessageFactory.buildPSAIdentifyPatientRequest())
                  .responseMessage(
                      HttpMessageFactory.buildStandardIdentifyPatientResponse(fileName))
                  .build())
          .build();
    }
  }

  @Override
  public Response findDocuments(final FindDocumentsRequest request) {
    final var fileName = getFileNameFromRequestHeader();
    final var builder = SimulatorCommunicationDataBuilder.newInstance();

    if (XDS_DOCUMENT_ENTRY_CLASS_CODE_EPED.equals(request.xdsDocumentEntryClassCode())) {
      builder
          .requestMessage(HttpMessageFactory.buildEPEDFindDocumentRequest())
          .responseMessage(HttpMessageFactory.buildStandardFindDocumentResponseEPED(fileName));
    } else {
      builder
          .requestMessage(HttpMessageFactory.buildPSAFindDocumentRequest())
          .responseMessage(HttpMessageFactory.buildStandardFindDocumentResponsePSA(fileName));
    }

    return okResponseBuilder().entity(builder.build()).build();
  }

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

    return okResponseBuilder()
        .entity(
            SimulatorCommunicationDataBuilder.newInstance()
                .requestMessage(HttpMessageFactory.buildStandardRetrieveSetOfDocumentsRequest())
                .responseMessage(
                    HttpMessageFactory.buildRetrieveDocumentResponse(
                        RetrieveDocumentMessagesBuilder.buildFromRequestAndPatient(
                            request, patient),
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

  private String getFileNameFromRequestHeader() {
    return Optional.ofNullable(headers.getHeaderString(HEADER_X_NCPEH_MOCK_RESPONSE))
        .map(name -> name + FILE_EXTENSIONS_XML)
        .orElse(null);
  }

  private Patient getPatientFromRequestHeader() {
    return Optional.ofNullable(headers.getHeaderString(HEADER_X_NCPEH_MOCK_PATIENT))
        .map(
            base64json -> {
              try {
                return mapper.readValue(Base64.getDecoder().decode(base64json), Patient.class);
              } catch (final IOException e) {
                // nothing to do
                log.warn("Failed to parse patient from header", e);
              }
              return null;
            })
        .orElse(null);
  }
}
