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

package de.gematik.ncpeh.api.mock;

import static org.junit.jupiter.api.Assertions.*;

import de.gematik.ncpeh.api.request.FindDocumentsRequest;
import de.gematik.ncpeh.api.request.IdentifyPatientRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.response.SimulatorCommunicationData;
import org.junit.jupiter.api.Test;

class NCPeHMockApiImplTest {

  NCPeHMockApiImpl tstObj = new NCPeHMockApiImpl();

  @Test
  void identifyPatient() {
    var response =
        assertDoesNotThrow(
            () ->
                tstObj.identifyPatient(
                    new IdentifyPatientRequest(null, null, null, null, null, null, null)),
            "Method NCPeHMockApiImpl.identifyPatient threw an exception");

    assertEquals(
        SimulatorCommunicationData.class,
        response.getEntity().getClass(),
        "Entity of the response was not of expected type");
  }

  @Test
  void findDocuments() {
    var response =
        assertDoesNotThrow(
            () ->
                tstObj.findDocuments(new FindDocumentsRequest(null, null, null, null, null, null)),
            "Method NCPeHMockApiImpl.findDocuments threw an exception");

    assertEquals(
        SimulatorCommunicationData.class,
        response.getEntity().getClass(),
        "Entity of the response was not of expected type");
  }

  @Test
  void retrieveDocument() {
    var response =
        assertDoesNotThrow(
            () ->
                tstObj.retrieveDocument(
                    new RetrieveDocumentRequest(null, null, null, null, null, null, null)),
            "Method NCPeHMockApiImpl.retrieveDocument threw an exception");

    assertEquals(
        SimulatorCommunicationData.class,
        response.getEntity().getClass(),
        "Entity of the response was not of expected type");
  }
}
