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

import static de.gematik.ncpeh.api.mock.NCPeHMockApiImpl.HEADER_X_NCPEH_MOCK_RESPONSE;
import static de.gematik.ncpeh.api.mock.TestUtils.loadFromJsonResource;
import static de.gematik.ncpeh.api.mock.TestUtils.readResourceFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import de.gematik.ncpeh.api.NcpehSimulatorApi;
import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.ProvideAndRegisterSetOfDocumentsRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import de.gematik.ncpeh.api.response.SimulatorCommunicationData;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.xmlunit.matchers.CompareMatcher;

@SpringBootTest(
    classes = NCPeHMockApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class NCPeHMockApiImplTest {

  @LocalServerPort private int port;

  final JacksonJsonProvider provider = createJacksonJsonProvider();

  NcpehSimulatorApi api;

  @BeforeEach
  void setUp() {
    api =
        JAXRSClientFactory.create(
            "http://localhost:" + port + "/rest", NcpehSimulatorApi.class, List.of(provider));
  }

  @ParameterizedTest
  @CsvSource({
    "null,PRPA_IN201306UV02.xml",
    "some,PRPA_IN201306UV02.xml",
    "PRPA_IN201306UV02_4,PRPA_IN201306UV02_4.xml"
  })
  void identifyPatientTest(final String header, final String expected) {
    // Arrange
    if (!"null".equals(header)) {
      ((Client) api).header(HEADER_X_NCPEH_MOCK_RESPONSE, header);
    }
    final var request =
        loadFromJsonResource(
            IdentifyPatientRequest.class, this.getClass(), "IdentifyPatientRequest.json");

    // Act
    final var response =
        assertDoesNotThrow(
            () -> api.identifyPatient(request),
            "Method NCPeHMockApiImpl.identifyPatient threw an exception");

    // Assert
    assertTrue(HttpStatus.valueOf(response.getStatus()).is2xxSuccessful());
    final var simulatorComData = response.readEntity(SimulatorCommunicationData.class);
    assertNotNull(simulatorComData);

    final var requestBody =
        new String(
            simulatorComData.requestSend().messageContent().httpBody(), StandardCharsets.UTF_8);
    var expectedData = readResourceFile(this.getClass(), "PRPA_IN201305UV02_298.xml");
    assertThat(
        requestBody, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());

    final var body =
        new String(
            simulatorComData.responseReceived().messageContent().httpBody(),
            StandardCharsets.UTF_8);
    expectedData = readResourceFile(this.getClass(), expected);

    assertThat(body, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  private static JacksonJsonProvider createJacksonJsonProvider() {

    return new JacksonJsonProvider()
        .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @ParameterizedTest
  @CsvSource({
    "null,AdhocQueryResponsePSA.xml",
    "some,AdhocQueryResponsePSA.xml",
    "AdhocQueryResponse_010,AdhocQueryResponse_010.xml"
  })
  void findDocumentsTest(final String header, final String expected) {
    // Arrange
    if (!"null".equals(header)) {
      ((Client) api).header(HEADER_X_NCPEH_MOCK_RESPONSE, header);
    }
    final var request =
        loadFromJsonResource(
            FindDocumentsRequest.class, this.getClass(), "FindDocumentsRequestPSA.json");

    // Act
    final var response =
        assertDoesNotThrow(
            () -> api.findDocuments(request),
            "Method NCPeHMockApiImpl.findDocuments threw an exception");

    // Assert
    assertTrue(HttpStatus.valueOf(response.getStatus()).is2xxSuccessful());
    final var simulatorComData = response.readEntity(SimulatorCommunicationData.class);
    assertNotNull(simulatorComData);

    final var requestBody =
        new String(
            simulatorComData.requestSend().messageContent().httpBody(), StandardCharsets.UTF_8);
    var expectedData = readResourceFile(this.getClass(), "AdhocQueryRequestPSA.xml");
    assertThat(
        requestBody, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());

    final var body =
        new String(
            simulatorComData.responseReceived().messageContent().httpBody(),
            StandardCharsets.UTF_8);
    expectedData = readResourceFile(this.getClass(), expected);

    assertThat(body, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @ParameterizedTest
  @CsvSource({
    "null,AdhocQueryResponseEPED.xml",
    "some,AdhocQueryResponseEPED.xml",
    "AdhocQueryResponseEPED.xml,AdhocQueryResponseEPED.xml"
  })
  void findDocumentsTest2(final String header, final String expected) {
    // Arrange
    if (!"null".equals(header)) {
      ((Client) api).header(HEADER_X_NCPEH_MOCK_RESPONSE, header);
    }
    final var request =
        loadFromJsonResource(
            FindDocumentsRequest.class, this.getClass(), "FindDocumentsRequestEPED.json");

    // Act
    final var response =
        assertDoesNotThrow(
            () -> api.findDocuments(request),
            "Method NCPeHMockApiImpl.findDocuments threw an exception");

    // Assert
    assertTrue(HttpStatus.valueOf(response.getStatus()).is2xxSuccessful());
    final var simulatorComData = response.readEntity(SimulatorCommunicationData.class);
    assertNotNull(simulatorComData);

    final var requestBody =
        new String(
            simulatorComData.requestSend().messageContent().httpBody(), StandardCharsets.UTF_8);
    var expectedData = readResourceFile(this.getClass(), "AdhocQueryRequestEPED.xml");
    assertThat(
        requestBody, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());

    final var body =
        new String(
            simulatorComData.responseReceived().messageContent().httpBody(),
            StandardCharsets.UTF_8);
    expectedData = readResourceFile(this.getClass(), expected);

    assertThat(body, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @ParameterizedTest
  @CsvSource({
    "null,RetrieveDocumentSetResponse.xml",
    "some,RetrieveDocumentSetResponse.xml",
    "RetrieveDocumentSetResponse_020,RetrieveDocumentSetResponse_020.xml"
  })
  void retrieveDocumentTest(final String header, final String expected) {
    // Arrange
    if (!"null".equals(header)) {
      ((Client) api).header(HEADER_X_NCPEH_MOCK_RESPONSE, header);
    }
    final var request =
        loadFromJsonResource(
            RetrieveDocumentRequest.class, this.getClass(), "RetrieveDocumentRequest.json");

    // Act
    final var response =
        assertDoesNotThrow(
            () -> api.retrieveDocument(request),
            "Method NCPeHMockApiImpl.retrieveDocument threw an exception");

    // Assert
    assertTrue(HttpStatus.valueOf(response.getStatus()).is2xxSuccessful());
    final var simulatorComData = response.readEntity(SimulatorCommunicationData.class);
    assertNotNull(simulatorComData);

    final var requestBody =
        new String(
            simulatorComData.requestSend().messageContent().httpBody(), StandardCharsets.UTF_8);
    var expectedData = readResourceFile(this.getClass(), "RetrieveDocumentSetRequest.xml");
    assertThat(
        requestBody, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());

    final var body =
        new String(
            simulatorComData.responseReceived().messageContent().httpBody(),
            StandardCharsets.UTF_8);

    log.debug("Response body: {}", body);

    expectedData = readResourceFile(this.getClass(), expected);
    assertThat(body, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @ParameterizedTest
  @CsvSource({
    "null,RetrieveDocumentSetOfDocumentsResponse.xml",
    "some,RetrieveDocumentSetOfDocumentsResponse.xml",
    "RetrieveDocumentSetResponse_020,RetrieveDocumentSetResponse_020.xml"
  })
  void retrieveSetOfDocumentsTest(final String header, final String expected) {
    // Arrange
    if (!"null".equals(header)) {
      ((Client) api).header(HEADER_X_NCPEH_MOCK_RESPONSE, header);
    }
    final var request =
        loadFromJsonResource(
            RetrieveSetOfDocumentsRequest.class,
            this.getClass(),
            "RetrieveSetOfDocumentsRequest.json");

    // Act
    final var response =
        assertDoesNotThrow(
            () -> api.retrieveSetOfDocuments(request),
            "Method NCPeHMockApiImpl.retrieveSetOfDocuments threw an exception");

    // Assert
    assertTrue(HttpStatus.valueOf(response.getStatus()).is2xxSuccessful());
    final var simulatorComData = response.readEntity(SimulatorCommunicationData.class);
    assertNotNull(simulatorComData);

    final var requestBody =
        new String(
            simulatorComData.requestSend().messageContent().httpBody(), StandardCharsets.UTF_8);
    var expectedData = readResourceFile(this.getClass(), "RetrieveDocumentSetRequest.xml");
    assertThat(
        requestBody, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());

    final var body =
        new String(
            simulatorComData.responseReceived().messageContent().httpBody(),
            StandardCharsets.UTF_8);

    log.debug("Response body: {}", body);

    expectedData = readResourceFile(this.getClass(), expected);
    assertThat(body, CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());
  }

  @ParameterizedTest
  @CsvSource({
    "null,ProvideAndRegisterDocumentSetResponse.xml",
    "some,ProvideAndRegisterDocumentSetResponse.xml"
  })
  void provideAndRegisterSetOfDocumentsTest(final String header, final String expected) {
    // Arrange
    if (!"null".equals(header)) {
      ((Client) api).header(HEADER_X_NCPEH_MOCK_RESPONSE, header);
    }
    final var request =
        loadFromJsonResource(
            ProvideAndRegisterSetOfDocumentsRequest.class,
            this.getClass(),
            "ProvideAndRegisterSetOfDocumentsRequest.json");

    // Act
    final var response =
        assertDoesNotThrow(
            () -> api.provideAndRegisterSetOfDocuments(request),
            "Method NCPeHMockApiImpl.provideAndRegisterSetOfDocuments threw an exception");

    // Assert
    assertTrue(HttpStatus.valueOf(response.getStatus()).is2xxSuccessful());
    final var simulatorComData = response.readEntity(SimulatorCommunicationData.class);
    assertNotNull(simulatorComData);

    final var requestBody =
        new String(
            simulatorComData.requestSend().messageContent().httpBody(), StandardCharsets.UTF_8);

    log.debug("Request body: {}", requestBody);

    final var expectedRequest =
        readResourceFile(this.getClass(), "ProvideAndRegisterDocumentSetRequest.xml");
    assertThat(
        requestBody,
        CompareMatcher.isSimilarTo(expectedRequest).ignoreWhitespace().ignoreComments());

    final var responseBody =
        new String(
            simulatorComData.responseReceived().messageContent().httpBody(),
            StandardCharsets.UTF_8);
    final var expectedResponse = readResourceFile(this.getClass(), expected);

    log.debug("Response body: {}", responseBody);

    assertThat(
        responseBody,
        CompareMatcher.isSimilarTo(expectedResponse).ignoreWhitespace().ignoreComments());
  }
}
