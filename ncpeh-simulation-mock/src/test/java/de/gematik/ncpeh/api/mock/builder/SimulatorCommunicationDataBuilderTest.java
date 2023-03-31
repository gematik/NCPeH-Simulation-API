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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimulatorCommunicationDataBuilderTest {

  @Test
  void build() {
    var tstObj =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.newInstance()
                    .requestMessage(HttpMessageFactory.buildStandardIdentifyPatientRequest())
                    .responseMessage(HttpMessageFactory.buildStandardIdentifyPatientResponse()),
            "Unexpected Exception thrown by SimulatorCommunicationDataBuilder");

    var result =
        assertDoesNotThrow(
            tstObj::build, "SimulatorCommunicationDataBuilder.build threw unexpected exception");

    assertNotNull(result, "Result of the builder is null");
    assertNotNull(result.requestSend(), "Request of the SimulatorCommunicationData object is null");
    assertNotNull(
        result.responseReceived(), "Response of the SimulatorCommunicationData object is null");
  }

  @Test
  void buildNull() {
    var tstObj =
        assertDoesNotThrow(
            SimulatorCommunicationDataBuilder::newInstance,
            "Unexpected Exception thrown by SimulatorCommunicationDataBuilder");

    var result =
        assertDoesNotThrow(
            tstObj::build, "SimulatorCommunicationDataBuilder.build threw unexpected exception");

    assertNotNull(result, "Result of the builder is null");
    assertNull(
        result.requestSend(), "Request of the SimulatorCommunicationData object is not null");
    assertNull(
        result.responseReceived(), "Response of the SimulatorCommunicationData object is not null");
  }

  @Test
  void wrapHttpMessageReq() {
    var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpMessage(
                    HttpMessageFactory.buildStandardIdentifyPatientRequest()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpMessage");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpMessage is null");
  }

  @Test
  void wrapHttpMessageResp() {
    var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpMessage(
                    HttpMessageFactory.buildStandardIdentifyPatientResponse()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpMessage");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpMessage is null");
  }

  @Test
  void wrapHttpRequest() {
    var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpRequest(
                    HttpMessageFactory.buildStandardIdentifyPatientRequest()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpRequest");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpRequest is null");
  }

  @Test
  void wrapHttpResponse() {
    var result =
        assertDoesNotThrow(
            () ->
                SimulatorCommunicationDataBuilder.wrapHttpResponse(
                    HttpMessageFactory.buildStandardIdentifyPatientResponse()),
            "Unexpected exception thrown by method SimulatorCommunicationDataBuilder.wrapHttpResponse");

    assertNotNull(
        result, "result of the method SimulatorCommunicationDataBuilder.wrapHttpResponse is null");
  }
}
