/*
 * Copyright 2023 gematik GmbH
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

import static org.junit.jupiter.api.Assertions.*;

import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo;
import java.io.FileNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

class HttpMessageFactoryTest {

  @Test
  void buildPSAIdentifyPatientRequestTest() {
    var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildPSAIdentifyPatientRequest,
            "Method HttpMessageFactory.buildPSAIdentifyPatientRequest threw exception");

    assertRequestProps(httpRequest);
  }

  @Test
  void buildEPEDIdentifyPatientRequestTest() {
    var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildEPEDIdentifyPatientRequest,
            "Method HttpMessageFactory.buildEPEDIdentifyPatientRequest threw exception");

    assertRequestProps(httpRequest);
  }

  @Test
  void buildStandardIdentifyPatientResponseTest() {
    var httpResponse =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardIdentifyPatientResponse,
            "Method HttpMessageFactory.buildStandardIdentifyPatientResponse threw exception");

    assertResponseProps(httpResponse);
  }

  @Test
  void buildStandardFindDocumentRequestTest() {
    var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardFindDocumentRequest,
            "Method HttpMessageFactory.buildStandardFindDocumentRequest threw exception");

    assertRequestProps(httpRequest);
  }

  @Test
  void buildStandardFindDocumentResponseTest() {
    var httpResponse =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardFindDocumentResponse,
            "Method HttpMessageFactory.buildStandardFindDocumentResponse threw exception");

    assertResponseProps(httpResponse);
  }

  @Test
  void buildStandardRetrieveDocumentRequest() {
    var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardRetrieveDocumentRequest,
            "Method HttpMessageFactory.buildStandardRetrieveDocumentRequest threw exception");

    assertRequestProps(httpRequest);
  }

  @Test
  void buildStandardRetrieveDocumentResponse() {
    var httpResponse =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardRetrieveDocumentResponse,
            "Method HttpMessageFactory.buildStandardRetrieveDocumentResponse threw exception");

    assertResponseProps(httpResponse);
  }

  @Test
  void readFileContentFromPathTest() {
    var result =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.readUTF8FileContentFromPath(
                    HttpMessageFactory.MESSAGES_FOLDER
                        + HttpMessageFactory.PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME));

    assertNotNull(result);
    assertTrue(result.contains("subjectOf1"));
  }

  @Test
  void readFileContentFromPathNoExistingFileTest() {
    var expectedException =
        assertThrows(
            FileNotFoundException.class,
            () ->
                HttpMessageFactory.readUTF8FileContentFromPath(
                    HttpMessageFactory.PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME + ".bak"));

    assertTrue(
        expectedException
            .getMessage()
            .contains(HttpMessageFactory.PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME));
  }

  @Test
  void findReadableFileResourceNoFileTest() {
    var packageName = this.getClass().getPackageName();
    var lowestPackage = packageName.substring(packageName.lastIndexOf(".") + 1);
    var expectedException =
        assertThrows(
            FileNotFoundException.class,
            () -> HttpMessageFactory.readUTF8FileContentFromPath(lowestPackage));

    assertTrue(expectedException.getMessage().contains(lowestPackage));
  }

  @Test
  void buildRetrieveDocumentRequestTest() {
    var testdata = retrieveDocumentTestdata().buildRequest();

    var result =
        assertDoesNotThrow(() -> HttpMessageFactory.buildRetrieveDocumentRequest(testdata));

    assertRequestProps(result);
  }

  @Test
  void buildRetrieveDocumentResponseTest() {
    var testdata = retrieveDocumentTestdata().buildResponse();

    var result =
        assertDoesNotThrow(() -> HttpMessageFactory.buildRetrieveDocumentResponse(testdata));

    assertResponseProps(result);
  }

  @SneakyThrows
  private void assertRequestProps(ClientHttpRequest httpRequest) {
    assertNotNull(httpRequest);
    assertEquals(HttpMethod.POST, httpRequest.getMethod(), "Wrong method in HTTP request");
    assertEquals(Constants.PSEUDO_URI, httpRequest.getURI(), "Wrong URI in HTTP request");
    assertNotNull(httpRequest.getHeaders(), "No HTTP headers present in response");
    assertFalse(httpRequest.getHeaders().isEmpty(), "No HTTP headers present in response");
    assertNotNull(httpRequest.getBody(), "No body present in HTTP request");
  }

  @SneakyThrows
  private void assertResponseProps(ClientHttpResponse httpResponse) {
    assertNotNull(httpResponse);
    assertEquals(HttpStatus.OK, httpResponse.getStatusCode(), "Wrong status in HTTP response");
    assertNotNull(httpResponse.getHeaders(), "No HTTP headers present in response");
    assertFalse(httpResponse.getHeaders().isEmpty(), "No HTTP headers present in response");
    assertNotNull(httpResponse.getBody());
  }

  private RetrieveDocumentMessagesBuilder retrieveDocumentTestdata() {
    return new RetrieveDocumentMessagesBuilder()
        .documentUniqueId("2.25.2350928502702" + CDALevelInfo.LEVEL_1.idMarker())
        .additionalDocumentUniqueId("2.25.2350928502702" + CDALevelInfo.LEVEL_3.idMarker())
        .homeCommunityId("urn:oid:2.16.17.710.850.1000.990.101")
        .repositoryUniqueId("1.2.276.0.76.3.1.91.2");
  }
}
