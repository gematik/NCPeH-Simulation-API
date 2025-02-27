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

import static de.gematik.ncpeh.api.mock.TestUtils.loadFromJsonResource;
import static de.gematik.ncpeh.api.mock.TestUtils.readResourceFile;
import static de.gematik.ncpeh.api.mock.builder.HttpMessageFactory.PSEUDO_URI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
            () -> HttpMessageFactory.buildStandardFindDocumentResponseEPED(null),
            "Method HttpMessageFactory.buildStandardFindDocumentResponseEPED threw exception");

    // Assert
    assertResponseProps(httpResponse);
    final var expectedData = readResourceFile(getClass(), "AdhocQueryResponseEPED.xml");
    assertThat(
        httpResponse.getBody(),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
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

    // Act
    final var result =
        assertDoesNotThrow(
            () -> HttpMessageFactory.buildRetrieveDocumentResponse(request, finalInput));

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
  void buildStandardRetrieveSetOfDocumentsResponseTest() {
    // Arrange
    // Act
    final var httpResponse =
        assertDoesNotThrow(
            () -> HttpMessageFactory.buildStandardRetrieveSetOfDocumentsResponse(null),
            "Method HttpMessageFactory.buildStandardRetrieveSetOfDocumentsResponse threw exception");

    // Assert
    assertResponseProps(httpResponse);

    final var expectedData = readResourceFile(getClass(), "RetrieveDocumentSetResponse.xml");
    assertThat(
        httpResponse.getBody(),
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
}
