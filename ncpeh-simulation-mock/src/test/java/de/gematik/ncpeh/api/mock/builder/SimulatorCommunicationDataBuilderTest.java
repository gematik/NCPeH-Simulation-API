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

package de.gematik.ncpeh.api.mock.builder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SimulatorCommunicationDataBuilderTest {

  @Test
  void buildPSA() {
    final var tstObj =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.newInstance()
                    .requestMessage(HttpMessageFactory.buildPSAIdentifyPatientRequest())
                    .responseMessage(HttpMessageFactory.buildStandardIdentifyPatientResponse(null)),
            "Unexpected Exception thrown by SimulatorCommunicationDataBuilder");

    final var result =
        assertDoesNotThrow(
            tstObj::build, "SimulatorCommunicationDataBuilder.build threw unexpected exception");

    assertNotNull(result, "Result of the builder is null");
    assertNotNull(result.requestSend(), "Request of the SimulatorCommunicationData object is null");
    assertNotNull(
        result.responseReceived(), "Response of the SimulatorCommunicationData object is null");
  }

  @Test
  void buildEPED() {
    final var tstObj =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.newInstance()
                    .requestMessage(HttpMessageFactory.buildEPEDIdentifyPatientRequest())
                    .responseMessage(HttpMessageFactory.buildStandardIdentifyPatientResponse(null)),
            "Unexpected Exception thrown by SimulatorCommunicationDataBuilder");

    final var result =
        assertDoesNotThrow(
            tstObj::build, "SimulatorCommunicationDataBuilder.build threw unexpected exception");

    assertNotNull(result, "Result of the builder is null");
    assertNotNull(result.requestSend(), "Request of the SimulatorCommunicationData object is null");
    assertNotNull(
        result.responseReceived(), "Response of the SimulatorCommunicationData object is null");
  }

  @Test
  void buildNull() {
    final var tstObj =
        assertDoesNotThrow(
            SimulatorCommunicationDataBuilder::newInstance,
            "Unexpected Exception thrown by SimulatorCommunicationDataBuilder");

    final var result =
        assertDoesNotThrow(
            tstObj::build, "SimulatorCommunicationDataBuilder.build threw unexpected exception");

    assertNotNull(result, "Result of the builder is null");
    assertNull(
        result.requestSend(), "Request of the SimulatorCommunicationData object is not null");
    assertNull(
        result.responseReceived(), "Response of the SimulatorCommunicationData object is not null");
  }

  @Test
  void wrapHttpMessageReqPSA() {
    final var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpMessage(
                    HttpMessageFactory.buildPSAIdentifyPatientRequest()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpMessage");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpMessage is null");
  }

  @Test
  void wrapHttpMessageReqEPED() {
    final var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpMessage(
                    HttpMessageFactory.buildEPEDIdentifyPatientRequest()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpMessage");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpMessage is null");
  }

  @Test
  void wrapHttpMessageResp() {
    final var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpMessage(
                    HttpMessageFactory.buildStandardIdentifyPatientResponse(null)),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpMessage");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpMessage is null");
  }

  @Test
  void wrapHttpRequestPSA() {
    final var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpRequest(
                    HttpMessageFactory.buildPSAIdentifyPatientRequest()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpRequest");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpRequest is null");
  }

  @Test
  void wrapHttpRequestEPeD() {
    final var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpRequest(
                    HttpMessageFactory.buildEPEDIdentifyPatientRequest()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpRequest");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpRequest is null");
  }

  @Test
  void wrapHttpResponse() {
    final var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpResponse(
                    HttpMessageFactory.buildStandardIdentifyPatientResponse(null)),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpResponse");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpResponse is null");
  }
}
