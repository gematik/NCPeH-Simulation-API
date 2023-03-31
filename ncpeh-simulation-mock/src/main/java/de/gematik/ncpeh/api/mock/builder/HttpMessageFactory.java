/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.ncpeh.api.mock.builder;

import static de.gematik.ncpeh.api.mock.builder.Constants.*;

import de.gematik.ncpeh.api.mock.http.PseudoHttpRequest;
import de.gematik.ncpeh.api.mock.http.PseudoHttpResponse;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

/** Static methods used to create test data, to be returned by API operations */
@UtilityClass
@Slf4j
@Accessors(fluent = true)
public final class HttpMessageFactory {

  public static final String RESOURCES_FOLDER = "src/main/resources/";

  public static final String SPRING_BOOT_CLASSES_FOLDER = "BOOT-INF/classes/";

  public static final String MESSAGES_FOLDER = "messages/";

  public static final String PATIENT_IDENTIFICATION_REQUEST_FILE_NAME = "PRPA_IN201305UV02.xml";

  public static final String PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME = "PRPA_IN201306UV02.xml";

  public static final String FIND_DOCUMENT_REQUEST_FILE_NAME = "AdhocQueryRequest.xml";

  public static final String FIND_DOCUMENT_RESPONSE_FILE_NAME = "AdhocQueryResponse.xml";

  public static final String RETRIEVE_DOCUMENT_REQUEST_FILE_NAME = "RetrieveDocumentRequest.xml";

  public static final String RETRIEVE_DOCUMENT_RESPONSE_FILE_NAME = "RetrieveDocumentResponse.xml";

  @Getter(lazy = true)
  private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

  /**
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#identifyPatient(IdentifyPatientRequest)} response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildStandardIdentifyPatientRequest() {
    return buildHttpRequest(PATIENT_IDENTIFICATION_REQUEST_FILE_NAME);
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
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#retrieveDocument(RetrieveDocumentRequest)} response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildStandardRetrieveDocumentRequest() {
    return buildHttpRequest(RETRIEVE_DOCUMENT_REQUEST_FILE_NAME);
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element for the
   * {@link de.gematik.ncpeh.api.NcpehSimulatorApi#identifyPatient(IdentifyPatientRequest)}
   * response.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardIdentifyPatientResponse() {
    return buildHttpResponse(PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME);
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element in the
   * context of the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#findDocuments(FindDocumentsRequest)} operation.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardFindDocumentResponse() {
    return buildHttpResponse(FIND_DOCUMENT_RESPONSE_FILE_NAME);
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element for the
   * {@link de.gematik.ncpeh.api.NcpehSimulatorApi#retrieveDocument(RetrieveDocumentRequest)}
   * response.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardRetrieveDocumentResponse() {
    return buildHttpResponse(RETRIEVE_DOCUMENT_RESPONSE_FILE_NAME);
  }

  /**
   * Read the content of a file at the given path into a String.<br>
   * The encoding of the file must be UTF-8.
   *
   * @param filePath path of the file to read
   * @return the file content as {@link String}
   */
  @SneakyThrows
  public static String readFileContentFromPath(final String filePath) {
    var fileResource =
        Optional.ofNullable(filePath)
            .map(HttpMessageFactory::getReadableFileResource)
            .or(
                () ->
                    Optional.ofNullable(filePath)
                        .map(fp -> RESOURCES_FOLDER + fp)
                        .map(HttpMessageFactory::getReadableFileResource))
            .or(
                () ->
                    Optional.ofNullable(filePath)
                        .map(fp -> ResourceUtils.CLASSPATH_URL_PREFIX + fp)
                        .map(HttpMessageFactory::getReadableFileResource))
            .or(
                () ->
                    Optional.ofNullable(filePath)
                        .map(
                            fp ->
                                ResourceUtils.CLASSPATH_URL_PREFIX
                                    + SPRING_BOOT_CLASSES_FOLDER
                                    + fp)
                        .map(HttpMessageFactory::getReadableFileResource))
            .or(
                () ->
                    Optional.ofNullable(filePath).map(HttpMessageFactory::findReadableFileResource))
            .orElseThrow(
                () ->
                    new FileNotFoundException(
                        "No file with path " + filePath + " found in the common locations"));

    return new String(fileResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  }

  // region private

  private static PseudoHttpRequest buildHttpRequest(@NonNull String bodyDataFilePath) {
    var httpRequest =
        new PseudoHttpRequest()
            .setMethod(HttpMethod.POST)
            .setURI(PSEUDO_URI)
            .setRequestBody(readFileContentToOutputStream(MESSAGES_FOLDER + bodyDataFilePath));

    var headers = httpRequest.getHeaders();

    headers.setAccept(List.of(MediaType.TEXT_XML, APPLICATION_SOAP_XML));
    headers.setContentType(APPLICATION_SOAP_XML);
    headers.setContentLength(
        Optional.ofNullable(httpRequest.getRequestBody())
            .map(ByteArrayOutputStream::size)
            .orElse(0));

    return httpRequest;
  }

  private static ByteArrayOutputStream readFileContentToOutputStream(@NonNull String filePath) {
    var body = new ByteArrayOutputStream();
    body.writeBytes(readFileContentFromPath(filePath).getBytes(StandardCharsets.UTF_8));
    return body;
  }

  private static PseudoHttpResponse buildHttpResponse(@NonNull String bodyDataFilePath) {
    var body =
        readFileContentFromPath(MESSAGES_FOLDER + bodyDataFilePath)
            .getBytes(StandardCharsets.UTF_8);

    try (var httpResponse =
        new PseudoHttpResponse(HttpStatus.OK).setBody(new ByteArrayInputStream(body))) {

      var headers = new HttpHeaders();
      headers.setContentType(APPLICATION_SOAP_XML);
      headers.setAccept(List.of(APPLICATION_SOAP_XML, MediaType.TEXT_XML));
      headers.setContentLength(body.length);

      return httpResponse.setHeaders(headers);
    }
  }

  private static Resource getReadableFileResource(@NonNull String filePath) {
    var resource = resourceLoader().getResource(filePath);
    log.debug("Current resource attempt: {}", resource);
    if (resource.exists() && resource.isReadable()) {
      return resource;
    } else {
      return null;
    }
  }

  @SneakyThrows
  private static Resource findReadableFileResource(@NonNull String filePath) {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "**");
    return Arrays.stream(resources)
        .filter(resource -> Objects.nonNull(resource.getFilename()))
        .filter(resource -> resource.getFilename().endsWith(filePath))
        .filter(Resource::isReadable)
        .findFirst()
        .orElse(null);
  }

  // endregion private
}
