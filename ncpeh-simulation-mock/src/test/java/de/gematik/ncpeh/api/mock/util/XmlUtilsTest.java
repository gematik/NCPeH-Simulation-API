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

package de.gematik.ncpeh.api.mock.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType.DocumentRequest;
import lombok.SneakyThrows;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import org.junit.jupiter.api.Test;

class XmlUtilsTest {

  @Test
  void marshalTest() {
    final var testdata = retrieveDocumentTestdata().buildRequest();

    final var result = assertDoesNotThrow(() -> XmlUtils.marshal(testdata));

    assertNotNull(result);

    final var roundTripResult =
        assertDoesNotThrow(() -> XmlUtils.unmarshal(testdata.getClass(), result));

    assertNotNull(roundTripResult);
    assertEquals(testdata.getDocumentRequest().size(), roundTripResult.getDocumentRequest().size());
    assertArrayEquals(
        testdata.getDocumentRequest().stream().map(DocumentRequest::getDocumentUniqueId).toArray(),
        roundTripResult.getDocumentRequest().stream()
            .map(DocumentRequest::getDocumentUniqueId)
            .toArray());
  }

  @SneakyThrows
  @Test
  void unmarshalTest() {
    try (final var testdata =
        HttpMessageFactory.readMessageFileSafely(
            HttpMessageFactory.FIND_DOCUMENT_REQUEST_FILE_NAME)) {

      final var result =
          assertDoesNotThrow(
              () -> XmlUtils.unmarshal(AdhocQueryRequest.class, testdata.readAllBytes()));

      assertNotNull(result);
      assertNotNull(result.getResponseOption());
      assertNotNull(result.getAdhocQuery());
      assertFalse(result.getAdhocQuery().getSlot().isEmpty());

      final var roundTripResult = assertDoesNotThrow(() -> XmlUtils.marshal(result));

      assertNotNull(roundTripResult);
    }
  }

  private RetrieveDocumentMessagesBuilder retrieveDocumentTestdata() {
    return new RetrieveDocumentMessagesBuilder()
        .documentUniqueId("2.25.2350928502702^2.25.2.PS.1")
        .additionalDocumentUniqueId("2.25.2350928502702^2.25.2.PS.2")
        .homeCommunityId("urn:oid:2.16.17.710.850.1000.990.101")
        .repositoryUniqueId("1.2.276.0.76.3.1.91.2");
  }
}
