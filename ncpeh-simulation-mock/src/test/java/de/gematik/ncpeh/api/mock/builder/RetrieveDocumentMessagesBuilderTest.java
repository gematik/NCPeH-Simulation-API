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

import static org.junit.jupiter.api.Assertions.*;

import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import org.junit.jupiter.api.Test;

class RetrieveDocumentMessagesBuilderTest {

  private final RetrieveDocumentRequest testdata =
      new RetrieveDocumentRequest(null, null, null, null, "A", "B^PS.PDF", "B^PS.XML", "D");

  @Test
  void useDataFromTest() {
    var tstObj = new RetrieveDocumentMessagesBuilder();

    assertDoesNotThrow(() -> tstObj.useDataFrom(testdata));

    assertEquals(testdata.documentUniqueId(), tstObj.documentUniqueId());
    assertEquals(testdata.additionalDocumentUniqueId(), tstObj.additionalDocumentUniqueId());
    assertEquals(testdata.homeCommunityId(), tstObj.homeCommunityId());
    assertEquals(testdata.repositoryUniqueId(), tstObj.repositoryUniqueId());
  }

  @Test
  void buildRequestTest() {
    var tstObj = new RetrieveDocumentMessagesBuilder();
    tstObj.useDataFrom(testdata);

    var result = assertDoesNotThrow(tstObj::buildRequest);

    assertNotNull(result);
    assertEquals(2, result.getDocumentRequest().size());
    var docRequests = result.getDocumentRequest();
    assertTrue(
        docRequests.stream()
            .anyMatch(dr -> testdata.documentUniqueId().equals(dr.getDocumentUniqueId())));
    assertTrue(
        docRequests.stream()
            .anyMatch(
                dr -> testdata.additionalDocumentUniqueId().equals(dr.getDocumentUniqueId())));
    assertTrue(
        docRequests.stream()
            .allMatch(dr -> testdata.homeCommunityId().equals(dr.getHomeCommunityId())));
    assertTrue(
        docRequests.stream()
            .allMatch(dr -> testdata.repositoryUniqueId().equals(dr.getRepositoryUniqueId())));
  }

  @Test
  void buildResponseTest() {

    var tstObj = new RetrieveDocumentMessagesBuilder();
    tstObj.useDataFrom(testdata);

    var result = assertDoesNotThrow(tstObj::buildResponse);

    assertNotNull(result);
    assertEquals(2, result.getDocumentResponse().size());
    var docResponses = result.getDocumentResponse();
    assertTrue(
        docResponses.stream()
            .anyMatch(dr -> testdata.documentUniqueId().equals(dr.getDocumentUniqueId())));
    assertTrue(
        docResponses.stream()
            .anyMatch(
                dr -> testdata.additionalDocumentUniqueId().equals(dr.getDocumentUniqueId())));
    assertTrue(
        docResponses.stream()
            .allMatch(dr -> testdata.homeCommunityId().equals(dr.getHomeCommunityId())));
    assertTrue(
        docResponses.stream()
            .allMatch(dr -> testdata.repositoryUniqueId().equals(dr.getRepositoryUniqueId())));
    assertTrue(
        docResponses.stream()
            .allMatch(
                dr ->
                    CDALevelInfo.fromDocumentUniqueId(dr.getDocumentUniqueId())
                        .mimeType()
                        .equals(dr.getMimeType())));
  }
}
