/*
 * Copyright (c) 2024-2025 gematik GmbH
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
 */

package de.gematik.ncpeh.api.mock.builder;

import static de.gematik.ncpeh.api.mock.builder.SoapMessageFactory.createSoapMessage;

import de.gematik.ncpeh.api.mock.http.PseudoHttpRequest;
import de.gematik.ncpeh.api.mock.http.PseudoHttpResponse;
import de.gematik.ncpeh.api.mock.util.XmlUtils;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/** Static methods used to create test data, to be returned by API operations */
@UtilityClass
@Slf4j
@Accessors(fluent = true)
public final class HttpMessageFactory {

  public static final String MESSAGES_FOLDER = "messages/";

  public static final String PATIENT_IDENTIFICATION_PSA_REQUEST_FILE_NAME =
      "PRPA_IN201305UV02_298.xml";

  public static final String PATIENT_IDENTIFICATION_EPED_REQUEST_FILE_NAME =
      "PRPA_IN201305UV02_299.xml";

  public static final String PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME = "PRPA_IN201306UV02.xml";

  public static final String FIND_DOCUMENT_REQUEST_FILE_NAME = "AdhocQueryRequest.xml";

  public static final String FIND_DOCUMENT_RESPONSE_FILE_NAME = "AdhocQueryResponse.xml";

  public static final URI PSEUDO_URI = URI.create("http://pseudoDn:12345/pseudoPath");

  private static final MediaType APPLICATION_SOAP_XML =
      new MediaType("application", "soap+xml", StandardCharsets.UTF_8);

  @Getter(lazy = true)
  private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

  /**
   * Build an HTTP request for PSA to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#identifyPatient(IdentifyPatientRequest)} response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildPSAIdentifyPatientRequest() {
    return buildHttpRequest(PATIENT_IDENTIFICATION_PSA_REQUEST_FILE_NAME);
  }

  /**
   * Build an HTTP request for EPED to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#identifyPatient(IdentifyPatientRequest)} response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildEPEDIdentifyPatientRequest() {
    return buildHttpRequest(PATIENT_IDENTIFICATION_EPED_REQUEST_FILE_NAME);
  }

  /**
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#findDocuments(FindDocumentsRequest)} response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildStandardFindDocumentRequest() {
    return buildHttpRequest(FIND_DOCUMENT_REQUEST_FILE_NAME);
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element for the
   * {@link de.gematik.ncpeh.api.NcpehSimulatorApi#identifyPatient(IdentifyPatientRequest)}
   * response.
   *
   * @return {@link PseudoHttpResponse}
   */
  @SneakyThrows
  public static PseudoHttpResponse buildStandardIdentifyPatientResponse(final String fileName) {
    return buildHttpResponse(
        readMessageFileSafely(fileName, PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME));
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element in the
   * context of the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#findDocuments(FindDocumentsRequest)} operation.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardFindDocumentResponse(final String fileName) {
    return buildHttpResponse(readMessageFileSafely(fileName, FIND_DOCUMENT_RESPONSE_FILE_NAME));
  }

  public static PseudoHttpRequest buildRetrieveDocumentRequest(
      final RetrieveDocumentRequest request) {
    final var iheMsgBuilder = new RetrieveDocumentMessagesBuilder().useDataFrom(request);
    final var body = XmlUtils.marshal(iheMsgBuilder.buildRequest());
    return buildHttpRequest(new ByteArrayInputStream(body));
  }

  public static PseudoHttpResponse buildRetrieveDocumentResponse(
      final RetrieveDocumentRequest request, final String fileName) {
    return buildHttpResponse(
        Optional.ofNullable(readMessageFile(fileName))
            .orElseGet(
                () -> {
                  final var iheMsgBuilder =
                      new RetrieveDocumentMessagesBuilder().useDataFrom(request);
                  final var body = XmlUtils.marshal(iheMsgBuilder.buildResponse());
                  return new ByteArrayInputStream(body);
                }));
  }

  public static InputStream readMessageFileSafely(final String filePath) {
    return readMessageFileSafely(filePath, null);
  }

  private static InputStream readMessageFile(final String filePath) {
    return readMessageFile(filePath, null);
  }

  @SneakyThrows
  public static InputStream readMessageFileSafely(
      final String filePath, final String defaultFilePath) {
    return Optional.ofNullable(readMessageFile(filePath, defaultFilePath))
        .orElseThrow(
            () ->
                new FileNotFoundException(
                    String.format("Message files not found: %s & %s", filePath, defaultFilePath)));
  }

  @SneakyThrows
  private static InputStream readMessageFile(final String filePath, final String defaultFilePath) {
    return Optional.ofNullable(filePath)
        .map(
            fp ->
                HttpMessageFactory.class.getClassLoader().getResourceAsStream(MESSAGES_FOLDER + fp))
        .or(
            () ->
                Optional.ofNullable(defaultFilePath)
                    .map(
                        fp ->
                            HttpMessageFactory.class
                                .getClassLoader()
                                .getResourceAsStream(MESSAGES_FOLDER + fp)))
        .orElse(null);
  }

  // region private

  @SneakyThrows
  private static PseudoHttpRequest buildHttpRequest(@NonNull final String bodyDataFilePath) {
    return buildHttpRequest(readMessageFileSafely(bodyDataFilePath));
  }

  @SneakyThrows
  private static PseudoHttpRequest buildHttpRequest(@NonNull final InputStream bodyData) {
    final var httpRequest =
        new PseudoHttpRequest()
            .setMethod(HttpMethod.POST)
            .setURI(PSEUDO_URI)
            .setRequestBody(createSoapMessage(bodyData));

    final var headers = httpRequest.getHeaders();

    headers.setAccept(List.of(MediaType.TEXT_XML, APPLICATION_SOAP_XML));
    headers.setContentType(APPLICATION_SOAP_XML);
    headers.setContentLength(
        Optional.ofNullable(httpRequest.getRequestBody())
            .map(ByteArrayOutputStream::size)
            .orElse(0));

    return httpRequest;
  }

  @SneakyThrows
  private static PseudoHttpResponse buildHttpResponse(@NonNull final InputStream bodyData) {
    try (final var httpResponse = new PseudoHttpResponse(HttpStatus.OK)) {

      httpResponse.setBody(new ByteArrayInputStream(createSoapMessage(bodyData).toByteArray()));

      final var headers = new HttpHeaders();
      headers.setContentType(APPLICATION_SOAP_XML);
      headers.setAccept(List.of(APPLICATION_SOAP_XML, MediaType.TEXT_XML));
      headers.setContentLength(httpResponse.getBody().available());

      return httpResponse.setHeaders(headers);
    }
  }
  // endregion private
}
