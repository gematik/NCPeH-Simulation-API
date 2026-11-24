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

package de.gematik.ncpeh.api.mock.builder;

import static de.gematik.ncpeh.api.mock.builder.SoapMessageFactory.createSoapMessage;

import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.api.mock.data.Patient;
import de.gematik.ncpeh.api.mock.data.PatientImpl;
import de.gematik.ncpeh.api.mock.data.PersonName;
import de.gematik.ncpeh.api.mock.http.PseudoHttpRequest;
import de.gematik.ncpeh.api.mock.http.PseudoHttpResponse;
import de.gematik.ncpeh.api.mock.util.XmlUtils;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.ProvideAndRegisterSetOfDocumentsRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/** Static methods used to create test data, to be returned by API operations */
@Slf4j
@UtilityClass
@Accessors(fluent = true)
public final class HttpMessageFactory {

  public static final String MESSAGES_FOLDER = "messages/";

  public static final String PATIENT_IDENTIFICATION_PSA_REQUEST_FILE_NAME =
      "PRPA_IN201305UV02_298.xml";

  public static final String PATIENT_IDENTIFICATION_EPED_REQUEST_FILE_NAME =
      "PRPA_IN201305UV02_299.xml";

  public static final String PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME = "PRPA_IN201306UV02.xml";

  private static final String SOAP_FAULT_ELEMENT_NAME = "Fault";

  /**
   * The insurant described by the standard response template. Their data is replaced by the
   * insurant the caller announces in the request header, see {@link
   * #buildStandardIdentifyPatientResponse(String, Patient)}.
   */
  private static final Patient TEMPLATE_PATIENT =
      new PatientImpl(
          new PersonName()
              .titles("Gräfin")
              .givennames("Maude Adelheid Lilo Johanna")
              .lastnames("GõdofskýTEST-ONLY"),
          LocalDate.of(1967, 6, 30),
          "X110571344",
          "ABC123");

  private static final DateTimeFormatter BIRTH_TIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  private static final String PREFIX_ELEMENT_FORMAT = "<ns6:prefix>%s</ns6:prefix>";

  public static final String FIND_DOCUMENT_EPED_REQUEST_FILE_NAME = "AdhocQueryRequestEPED.xml";

  public static final String FIND_DOCUMENT_PSA_REQUEST_FILE_NAME = "AdhocQueryRequestPSA.xml";

  public static final String FIND_DOCUMENT_PSA_RESPONSE_FILE_NAME = "AdhocQueryResponsePSA.xml";

  public static final String FIND_DOCUMENT_EPED_RESPONSE_FILE_NAME = "AdhocQueryResponseEPED.xml";

  public static final String RETRIEVE_DOCUMENT_SET_REQUEST_FILE_NAME =
      "RetrieveDocumentSetRequest.xml";

  public static final String PROVIDE_AND_REGISTER_DOCUMENT_SET_REQUEST_FILE_NAME =
      "ProvideAndRegisterDocumentSetRequest.xml";

  public static final String PROVIDE_AND_REGISTER_DOCUMENT_SET_RESPONSE_FILE_NAME =
      "ProvideAndRegisterDocumentSetResponse.xml";

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
  public static PseudoHttpRequest buildEPEDFindDocumentRequest() {
    return buildHttpRequest(FIND_DOCUMENT_EPED_REQUEST_FILE_NAME);
  }

  /**
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#findDocuments(FindDocumentsRequest)} response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildPSAFindDocumentRequest() {
    return buildHttpRequest(FIND_DOCUMENT_PSA_REQUEST_FILE_NAME);
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
    return buildStandardIdentifyPatientResponse(fileName, null);
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element for the
   * {@link de.gematik.ncpeh.api.NcpehSimulatorApi#identifyPatient(IdentifyPatientRequest)}
   * response.
   *
   * <p>The standard response template carries placeholders for the identified insurant. They are
   * filled with the patient the caller announced in the request header, so that scenarios can use
   * any insurant. Without that header the default insurant is returned.
   *
   * @param fileName optional response template name
   * @param patient optional patient to inject into the successful identifyPatient response
   * @return {@link PseudoHttpResponse}
   */
  @SneakyThrows
  public static PseudoHttpResponse buildStandardIdentifyPatientResponse(
      final String fileName, final Patient patient) {
    final var template =
        new String(
            readMessageFileSafely(fileName, PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME)
                .readAllBytes(),
            StandardCharsets.UTF_8);
    final var body = null == patient ? template : replaceTemplatePatient(template, patient);
    return buildHttpResponse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Replaces the data of the insurant described by the standard response template with the data of
   * the given insurant. Response templates of error scenarios describe their own insurant and are
   * left untouched.
   *
   * @param template contents of the response template
   * @param patient the insurant to answer with
   * @return the response body for that insurant
   */
  private static String replaceTemplatePatient(final String template, final Patient patient) {
    final var templateName = TEMPLATE_PATIENT.name();
    final var name = Optional.ofNullable(patient.name()).orElseGet(PersonName::new);
    return template
        .replace(TEMPLATE_PATIENT.kvnr(), orTemplateValue(patient.kvnr(), TEMPLATE_PATIENT.kvnr()))
        .replace(
            TEMPLATE_PATIENT.accessCode(),
            orTemplateValue(patient.accessCode(), TEMPLATE_PATIENT.accessCode()))
        // an insurant without titles must not be answered with an empty prefix element
        .replace(
            PREFIX_ELEMENT_FORMAT.formatted(templateName.titles()),
            null == name.titles() || name.titles().isBlank()
                ? ""
                : PREFIX_ELEMENT_FORMAT.formatted(name.titles()))
        .replace(
            templateName.lastnames(), orTemplateValue(name.lastnames(), templateName.lastnames()))
        .replace(
            templateName.givennames(),
            orTemplateValue(name.givennames(), templateName.givennames()))
        .replace(
            BIRTH_TIME_FORMAT.format(TEMPLATE_PATIENT.birthdate()),
            Optional.ofNullable(patient.birthdate())
                .map(BIRTH_TIME_FORMAT::format)
                .orElseGet(() -> BIRTH_TIME_FORMAT.format(TEMPLATE_PATIENT.birthdate())));
  }

  private static String orTemplateValue(final String value, final String templateValue) {
    return null == value || value.isBlank() ? templateValue : value;
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element in the
   * context of the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#findDocuments(FindDocumentsRequest)} operation.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardFindDocumentResponsePSA(final String fileName) {
    return buildStandardFindDocumentResponsePSA(fileName, null);
  }

  public static PseudoHttpResponse buildStandardFindDocumentResponsePSA(
      final String fileName, final String officeName) {
    return buildHttpResponse(
        applyOfficeName(
            readMessageFileSafely(fileName, FIND_DOCUMENT_PSA_RESPONSE_FILE_NAME),
            officeName,
            "Nemocnice Havlíčkův Brod, příspěvková organizace"));
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element in the
   * context of the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#findDocuments(FindDocumentsRequest)} operation.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardFindDocumentResponseEPED(
      String fileName, Map<String, Medication> medicationByPrescriptionId) {
    return buildStandardFindDocumentResponseEPED(fileName, medicationByPrescriptionId, null);
  }

  public static PseudoHttpResponse buildStandardFindDocumentResponseEPED(
      String fileName, Map<String, Medication> medicationByPrescriptionId, String officeName) {
    var inputStream = readMessageFileSafely(fileName, FIND_DOCUMENT_EPED_RESPONSE_FILE_NAME);
    if (!StringUtils.isBlank(fileName)
        || medicationByPrescriptionId == null
        || medicationByPrescriptionId.isEmpty()) {
      return buildHttpResponse(
          applyOfficeName(inputStream, officeName, "Praxis Münchhausen-BohlenTEST-ONLY"));
    }

    try {
      byte[] documentAsBytes = inputStream.readAllBytes();
      AdhocQueryResponse adhocQueryResponse =
          XmlUtils.unmarshal(AdhocQueryResponse.class, documentAsBytes);

      XmlUtils.extendRegistryObjectList(adhocQueryResponse, medicationByPrescriptionId);

      return buildHttpResponse(
          applyOfficeName(
              new ByteArrayInputStream(XmlUtils.marshal(adhocQueryResponse)),
              officeName,
              "Praxis Münchhausen-BohlenTEST-ONLY"));

    } catch (IOException e) {
      throw new RuntimeException("Error reading AdhocQueryResponse", e);
    }
  }

  public static PseudoHttpRequest buildRetrieveDocumentRequest(
      final RetrieveDocumentRequest request) {
    final var iheMsgBuilder = RetrieveDocumentMessagesBuilder.fromRequest(request);
    final var body = XmlUtils.marshal(iheMsgBuilder.buildRequest());
    return buildHttpRequest(new ByteArrayInputStream(body));
  }

  public static PseudoHttpResponse buildRetrieveDocumentResponse(
      final RetrieveDocumentMessagesBuilder msgBuilder, final String fileName) {
    return buildHttpResponse(
        Optional.ofNullable(readMessageFile(fileName))
            .orElseGet(
                () -> {
                  final var body = XmlUtils.marshal(msgBuilder.buildResponse());
                  return new ByteArrayInputStream(body);
                }));
  }

  /**
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#retrieveSetOfDocuments(RetrieveSetOfDocumentsRequest)}
   * response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildStandardRetrieveSetOfDocumentsRequest() {
    return buildHttpRequest(readMessageFileSafely(RETRIEVE_DOCUMENT_SET_REQUEST_FILE_NAME));
  }

  /**
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend()} element for the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#provideAndRegisterSetOfDocuments(ProvideAndRegisterSetOfDocumentsRequest)}
   * response.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildStandardProvideAndRegisterSetOfDocumentsRequest() {
    return buildHttpRequest(
        readMessageFileSafely(PROVIDE_AND_REGISTER_DOCUMENT_SET_REQUEST_FILE_NAME));
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived()} element in the
   * context of the {@link
   * de.gematik.ncpeh.api.NcpehSimulatorApi#provideAndRegisterSetOfDocuments(ProvideAndRegisterSetOfDocumentsRequest)}
   * operation.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardProvideAndRegisterSetOfDocumentsResponse(
      final String fileName) {
    return buildHttpResponse(
        readMessageFileSafely(fileName, PROVIDE_AND_REGISTER_DOCUMENT_SET_RESPONSE_FILE_NAME));
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
                    "Message files not found: %s & %s".formatted(filePath, defaultFilePath)));
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
    final var body = bodyData.readAllBytes();

    try (final var httpResponse = new PseudoHttpResponse(statusOf(body))) {

      httpResponse.setBody(
          new ByteArrayInputStream(
              createSoapMessage(new ByteArrayInputStream(body)).toByteArray()));

      final var headers = new HttpHeaders();
      headers.setContentType(APPLICATION_SOAP_XML);
      headers.setAccept(List.of(APPLICATION_SOAP_XML, MediaType.TEXT_XML));
      headers.setContentLength(httpResponse.getBody().available());

      return httpResponse.setHeaders(headers);
    }
  }

  private static HttpStatus statusOf(final byte[] body) {
    return isSoapFault(body) ? HttpStatus.BAD_REQUEST : HttpStatus.OK;
  }

  private static boolean isSoapFault(final byte[] body) {
    try {
      final var factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setNamespaceAware(true);

      final var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(body));
      return SOAP_FAULT_ELEMENT_NAME.equals(document.getDocumentElement().getLocalName());
    } catch (final Exception e) {
      log.debug("Response body is no parsable XML document: {}", e.getMessage());
      return false;
    }
  }

  @SneakyThrows
  private static InputStream applyOfficeName(
      final InputStream bodyData, final String officeName, final String defaultOfficeName) {
    if (StringUtils.isBlank(officeName)) {
      return bodyData;
    }

    try (bodyData) {
      final var content = new String(bodyData.readAllBytes(), StandardCharsets.UTF_8);
      return new ByteArrayInputStream(
          content.replace(defaultOfficeName, officeName).getBytes(StandardCharsets.UTF_8));
    }
  }
  // endregion private
}
