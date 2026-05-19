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

import static de.gematik.ncpeh.api.mock.TestUtils.loadFromJsonResource;
import static de.gematik.ncpeh.api.mock.TestUtils.readResourceFile;
import static de.gematik.ncpeh.api.mock.builder.HttpMessageFactory.PSEUDO_URI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.api.mock.util.IheUtils;
import de.gematik.ncpeh.api.mock.util.XmlUtils;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import jakarta.xml.bind.JAXBElement;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.xmlunit.matchers.CompareMatcher;

@Slf4j
class HttpMessageFactoryTest {

  private static final String EVENT_CODE_CLASSIFICATION_SCHEME =
      "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
  private static final String SUBSTITUTION_CODE_SYSTEM_OID = "2.16.840.1.113883.5.1070";

  /**
   * Read the content of a file at the given path into a String.<br>
   * The encoding of the file must be UTF-8.
   *
   * @param is InputStream of the file
   * @return the file content as {@link String}
   */
  @SneakyThrows
  private static String toUTF8String(final InputStream is) {
    final var result = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    is.close();
    return result;
  }

  @Test
  void buildPSAIdentifyPatientRequestTest() {
    // Arrange
    // Act
    final var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildPSAIdentifyPatientRequest,
            "Method HttpMessageFactory.buildPSAIdentifyPatientRequest threw exception");

    // Assert
    assertRequestProps(httpRequest);
    final var expectedData = readResourceFile(getClass(), "PRPA_IN201305UV02_298.xml");
    assertThat(
        httpRequest.getRequestBody().toString(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildEPEDIdentifyPatientRequestTest() {
    // Arrange
    // Act
    final var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildEPEDIdentifyPatientRequest,
            "Method HttpMessageFactory.buildEPEDIdentifyPatientRequest threw exception");

    // Assert
    assertRequestProps(httpRequest);
    final var expectedData = readResourceFile(getClass(), "PRPA_IN201305UV02_299.xml");
    assertThat(
        httpRequest.getRequestBody().toString(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardIdentifyPatientResponseTest() {
    // Arrange
    // Act
    final var httpResponse =
        assertDoesNotThrow(
            () -> HttpMessageFactory.buildStandardIdentifyPatientResponse(null),
            "Method HttpMessageFactory.buildStandardIdentifyPatientResponse threw exception");

    // Assert
    assertResponseProps(httpResponse);

    final var expectedData = readResourceFile(getClass(), "PRPA_IN201306UV02.xml");
    assertThat(
        httpResponse.getBody(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildEPEDFindDocumentRequestTest() {
    // Arrange
    // Act
    final var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildEPEDFindDocumentRequest,
            "Method HttpMessageFactory.buildEPEDFindDocumentRequest threw exception");
    // Assert
    assertRequestProps(httpRequest);
    final var expectedData = readResourceFile(getClass(), "AdhocQueryRequestEPED.xml");
    assertThat(
        httpRequest.getRequestBody().toString(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildPSAFindDocumentRequestTest() {
    // Arrange
    // Act
    final var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildPSAFindDocumentRequest,
            "Method HttpMessageFactory.buildPSAFindDocumentRequest threw exception");
    // Assert
    assertRequestProps(httpRequest);
    final var expectedData = readResourceFile(getClass(), "AdhocQueryRequestPSA.xml");
    assertThat(
        httpRequest.getRequestBody().toString(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardFindDocumentResponsePSATest() {
    // Arrange
    // Act
    final var httpResponse =
        assertDoesNotThrow(
            () -> HttpMessageFactory.buildStandardFindDocumentResponsePSA(null),
            "Method HttpMessageFactory.buildStandardFindDocumentResponsePSA threw exception");

    // Assert
    assertResponseProps(httpResponse);
    final var expectedData = readResourceFile(getClass(), "AdhocQueryResponsePSA.xml");
    assertThat(
        httpResponse.getBody(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardFindDocumentResponseEPEDTest() {
    // Arrange
    // Act
    final var httpResponse =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.buildStandardFindDocumentResponseEPED(
                    null, Collections.emptySet()),
            "Method HttpMessageFactory.buildStandardFindDocumentResponseEPED threw exception");

    // Assert
    assertResponseProps(httpResponse);
    final var expectedData = readResourceFile(getClass(), "AdhocQueryResponseEPED.xml");
    assertThat(
        httpResponse.getBody(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardFindDocumentWithOneGivenPrescriptionIdAndCheckIdsAreInResponse() {
    // Arrange
    // Act
    String prescriptionId = "prescribedMedicationId1";
    final var httpResponse =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.buildStandardFindDocumentResponseEPED(
                    "AdhocQueryResponseEPED.xml", Set.of(prescriptionId)),
            "Method HttpMessageFactory.buildStandardFindDocumentResponseEPED threw exception");
    // Assert
    assertResponseProps(httpResponse);

    try {
      byte[] documentAsBytes = httpResponse.getBody().readAllBytes();
      byte[] soapBody = XmlUtils.extractSoapBody(documentAsBytes);
      AdhocQueryResponse adhocQueryResponse =
          XmlUtils.unmarshal(AdhocQueryResponse.class, soapBody);
      var documentIds = IheUtils.extractDocumentIdsFromAdhocQueryResponse(adhocQueryResponse);
      assertThat(documentIds, hasItem(containsString(prescriptionId)));
    } catch (Exception e) {
      throw new RuntimeException(
          "Error reading response body or unmarshalling AdhocQueryResponse", e);
    }
  }

  @Test
  void buildStandardFindDocumentWithThreeGivenPrescriptionIdAndCheckEachIdIsInResponseTwice() {
    // Arrange
    // Act
    Set<String> prescriptionIds =
        Set.of("prescribedMedicationId1", "prescribedMedicationId2", "prescribedMedicationId3");
    final var httpResponse =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.buildStandardFindDocumentResponseEPED(
                    "AdhocQueryResponseEPED.xml", prescriptionIds),
            "Method HttpMessageFactory.buildStandardFindDocumentResponseEPED threw exception");
    // Assert
    assertResponseProps(httpResponse);

    try {
      byte[] documentAsBytes = httpResponse.getBody().readAllBytes();
      byte[] soapBody = XmlUtils.extractSoapBody(documentAsBytes);
      AdhocQueryResponse adhocQueryResponse =
          XmlUtils.unmarshal(AdhocQueryResponse.class, soapBody);
      var documentIds = IheUtils.extractDocumentIdsFromAdhocQueryResponse(adhocQueryResponse);
      for (String prescriptionId : prescriptionIds) {
        assertThat(
            "PrescriptionId " + prescriptionId + " should appear exactly twice",
            documentIds.stream().filter(id -> id != null && id.contains(prescriptionId)).count(),
            is(2L));
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Error reading response body or unmarshalling AdhocQueryResponse", e);
    }
  }

  @Test
  void buildStandardFindDocumentResponseEPED_shouldContainSubstitutionClassificationIfForbidden() {
    String prescriptionId = "prescribedMedicationId1";
    final var medicationsByPrescriptionId =
        Map.of(prescriptionId, new Medication("TestMed", "11111111", false));
    final var httpResponse =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.buildStandardFindDocumentResponseEPED(
                    "AdhocQueryResponseEPED.xml",
                    medicationsByPrescriptionId.keySet(),
                    medicationsByPrescriptionId),
            "Method HttpMessageFactory.buildStandardFindDocumentResponseEPED threw exception");

    assertResponseProps(httpResponse);

    try {
      byte[] documentAsBytes = httpResponse.getBody().readAllBytes();
      byte[] soapBody = XmlUtils.extractSoapBody(documentAsBytes);
      AdhocQueryResponse adhocQueryResponse =
          XmlUtils.unmarshal(AdhocQueryResponse.class, soapBody);
      assertEquals(
          2L,
          countForbiddenSubstitutionClassifications(adhocQueryResponse, prescriptionId),
          "Expected one forbidden-substitution classification per generated document");
    } catch (Exception e) {
      throw new RuntimeException(
          "Error reading response body or unmarshalling AdhocQueryResponse", e);
    }
  }

  @Test
  void readMessageFileSafelyTest() {
    // Arrange
    // Act
    final var is =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.readMessageFileSafely(
                    HttpMessageFactory.PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME));

    // Assert
    assertNotNull(is);
    final var content = toUTF8String(is);
    final var expexted =
        assertDoesNotThrow(
            () -> readResourceFile(this.getClass(), "mPRPA_IN201306UV02.xml"),
            "Error reading file");

    assertThat(content, CompareMatcher.isSimilarTo(expexted).ignoreWhitespace());
  }

  @ParameterizedTest
  @CsvSource({
    "null,mPRPA_IN201306UV02.xml",
    "some,mPRPA_IN201306UV02.xml",
    "PRPA_IN201306UV02_4.xml,mPRPA_IN201306UV02_4.xml"
  })
  void readMessageFileSafelyTest2(String input, final String expected) {
    // Arrange
    if ("null".equals(input)) {
      input = null;
    }
    final String finalInput = input;

    // Act
    final var is =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.readMessageFileSafely(
                    finalInput, HttpMessageFactory.PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME));

    // Assert
    assertNotNull(is);
    final var content = toUTF8String(is);
    final var expexted =
        assertDoesNotThrow(() -> readResourceFile(this.getClass(), expected), "Error reading file");

    assertThat(content, CompareMatcher.isSimilarTo(expexted).ignoreWhitespace().ignoreComments());
  }

  @Test
  void readMessageFileSafelyNoExistingFileTest() {
    // Arrange
    // Act
    final var expectedException =
        assertThrows(
            FileNotFoundException.class,
            () -> HttpMessageFactory.readMessageFileSafely("notExistingFile.xml"));

    // Assert
    assertEquals(
        "Message files not found: notExistingFile.xml & null", expectedException.getMessage());
  }

  @Test
  void readMessageFileSafelyNoExistingFileTest2() {
    // Arrange
    // Act
    final var expectedException =
        assertThrows(
            FileNotFoundException.class,
            () ->
                HttpMessageFactory.readMessageFileSafely(
                    "notExistingFile.xml", "notExistingDefaultFile.xml"));

    // Assert
    assertEquals(
        "Message files not found: notExistingFile.xml & notExistingDefaultFile.xml",
        expectedException.getMessage());
  }

  @SneakyThrows
  @Test
  void buildRetrieveDocumentRequestTest() {
    // Arrange
    final var request =
        loadFromJsonResource(
            RetrieveDocumentRequest.class, this.getClass(), "RetrieveDocumentRequest.json");

    // Act
    final var result =
        assertDoesNotThrow(() -> HttpMessageFactory.buildRetrieveDocumentRequest(request));

    // Assert
    final var body = result.getBody().toString();
    log.info(body);
    assertRequestProps(result);

    final var expexted =
        assertDoesNotThrow(
            () -> readResourceFile(this.getClass(), "RetrieveDocumentSetRequest.xml"),
            "Error reading file");

    assertThat(body, CompareMatcher.isSimilarTo(expexted).ignoreWhitespace().ignoreComments());
  }

  @ParameterizedTest
  @CsvSource({
    "null,RetrieveDocumentSetResponse.xml",
    "some,RetrieveDocumentSetResponse.xml",
    "RetrieveDocumentSetResponse_020.xml,RetrieveDocumentSetResponse_020.xml"
  })
  void buildRetrieveDocumentResponseTest(String input, final String expected) {
    // Arrange
    if ("null".equals(input)) {
      input = null;
    }
    final String finalInput = input;
    final var request =
        loadFromJsonResource(
            RetrieveDocumentRequest.class, this.getClass(), "RetrieveDocumentRequest.json");
    final var builder = RetrieveDocumentMessagesBuilder.fromRequest(request);

    // Act
    final var result =
        assertDoesNotThrow(
            () -> HttpMessageFactory.buildRetrieveDocumentResponse(builder, finalInput));

    // Assert
    final var body = toUTF8String(result.getBody());
    assertResponseProps(result);

    final var expexted =
        assertDoesNotThrow(() -> readResourceFile(this.getClass(), expected), "Error reading file");

    assertThat(body, CompareMatcher.isSimilarTo(expexted).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardRetrieveSetOfDocumentsRequestTest() {
    // Arrange
    // Act
    final var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardRetrieveSetOfDocumentsRequest,
            "Method HttpMessageFactory.buildStandardRetrieveSetOfDocumentsRequest threw exception");
    // Assert
    assertRequestProps(httpRequest);
    final var expectedData = readResourceFile(getClass(), "RetrieveDocumentSetRequest.xml");
    assertThat(
        httpRequest.getRequestBody().toString(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardProvideAndRegisterSetOfDocumentsRequestTest() {
    // Arrange
    // Act
    final var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardProvideAndRegisterSetOfDocumentsRequest,
            "Method HttpMessageFactory.buildStandardProvideAndRegisterSetOfDocumentsRequest "
                + "threw exception");
    // Assert
    assertRequestProps(httpRequest);
    final var requestBody =
        assertDoesNotThrow(
            () -> httpRequest.getRequestBody().toString(StandardCharsets.UTF_8),
            "Error converting request body into a string using UTF-8 charset.");
    final var expectedData =
        readResourceFile(getClass(), "ProvideAndRegisterDocumentSetRequest.xml");
    assertThat(
        requestBody, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @Test
  void buildStandardProvideAndRegisterDocumentSetResponseTest() {
    // Arrange
    // Act
    final var httpResponse =
        assertDoesNotThrow(
            () -> HttpMessageFactory.buildStandardProvideAndRegisterSetOfDocumentsResponse(null),
            "Method HttpMessageFactory.buildStandardProvideAndRegisterSetOfDocumentsResponse "
                + "threw exception");

    // Assert
    assertResponseProps(httpResponse);

    final var expectedData =
        readResourceFile(getClass(), "ProvideAndRegisterDocumentSetResponse.xml");
    assertThat(
        httpResponse.getBody(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @SneakyThrows
  private void assertRequestProps(final ClientHttpRequest httpRequest) {
    assertNotNull(httpRequest);
    assertEquals(HttpMethod.POST, httpRequest.getMethod(), "Wrong method in HTTP request");
    assertEquals(PSEUDO_URI, httpRequest.getURI(), "Wrong URI in HTTP request");
    assertNotNull(httpRequest.getHeaders(), "No HTTP headers present in response");
    assertFalse(httpRequest.getHeaders().isEmpty(), "No HTTP headers present in response");
    assertNotNull(httpRequest.getBody(), "No body present in HTTP request");
  }

  @SneakyThrows
  private void assertResponseProps(final ClientHttpResponse httpResponse) {
    assertNotNull(httpResponse);
    assertEquals(HttpStatus.OK, httpResponse.getStatusCode(), "Wrong status in HTTP response");
    assertNotNull(httpResponse.getHeaders(), "No HTTP headers present in response");
    assertFalse(httpResponse.getHeaders().isEmpty(), "No HTTP headers present in response");
    assertNotNull(httpResponse.getBody());
  }

  private static long countForbiddenSubstitutionClassifications(
      AdhocQueryResponse adhocQueryResponse, String prescriptionId) {
    return adhocQueryResponse.getRegistryObjectList().getIdentifiable().stream()
        .filter(e -> ExtrinsicObjectType.class.equals(e.getDeclaredType()))
        .map(JAXBElement::getValue)
        .map(ExtrinsicObjectType.class::cast)
        .filter(
            eo ->
                eo.getExternalIdentifier().stream()
                    .anyMatch(
                        ei ->
                            ei.getValue() != null
                                && ei.getValue().contains(prescriptionId)
                                && ei.getValue().startsWith("1.2.276.0.76.4.299^")))
        .flatMap(eo -> eo.getClassification().stream())
        .filter(c -> EVENT_CODE_CLASSIFICATION_SCHEME.equals(c.getClassificationScheme()))
        .filter(c -> "N".equals(c.getNodeRepresentation()))
        .filter(
            c ->
                c.getSlot().stream()
                    .anyMatch(
                        slot ->
                            "codingScheme".equals(slot.getName())
                                && slot.getValueList() != null
                                && slot.getValueList()
                                    .getValue()
                                    .contains(SUBSTITUTION_CODE_SYSTEM_OID)))
        .count();
  }
}
