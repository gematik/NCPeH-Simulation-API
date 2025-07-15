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

import static de.gematik.ncpeh.api.mock.TestUtils.readResourceFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo;
import de.gematik.ncpeh.api.mock.data.Patient;
import de.gematik.ncpeh.api.mock.data.PatientImpl;
import de.gematik.ncpeh.api.mock.data.PersonName;
import de.gematik.ncpeh.api.request.DocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.xmlunit.matchers.CompareMatcher;

class RetrieveDocumentMessagesBuilderTest {

  private final RetrieveDocumentRequest testdata =
      new RetrieveDocumentRequest(null, null, null, null, "A", "B^PS.PDF", "B^PS.XML", "D");

  private final RetrieveSetOfDocumentsRequest testdata2 =
      new RetrieveSetOfDocumentsRequest(
          null, null, null, null, Set.of(new DocumentRequest("DD", "AA", "ID")));

  private static final Patient PATIENT =
      new PatientImpl(
          new PersonName()
              .titles("Gräfin")
              .lastnames("GõdofskýTEST-ONLY")
              .givennames("Maude Adelheid Lilo Johanna"),
          LocalDate.of(1967, 6, 30));

  @Test
  void buildFromRequestTest() {
    // Arrange & Act
    final var tstObj =
        assertDoesNotThrow(() -> RetrieveDocumentMessagesBuilder.buildFromRequest(testdata));

    // Assert
    assertEquals(2, tstObj.documentationInfos().size());
    final var doc1 = tstObj.documentationInfos().getFirst();
    assertEquals(testdata.documentUniqueId(), doc1.documentUniqueId());
    assertEquals(testdata.homeCommunityId(), doc1.homeCommunityId());
    assertEquals(testdata.repositoryUniqueId(), doc1.repositoryUniqueId());
    final var doc2 = tstObj.documentationInfos().get(1);
    assertEquals(testdata.additionalDocumentUniqueId(), doc2.documentUniqueId());
    assertEquals(testdata.homeCommunityId(), doc2.homeCommunityId());
    assertEquals(testdata.repositoryUniqueId(), doc2.repositoryUniqueId());
    assertNull(tstObj.patient());
  }

  @Test
  void buildFromRequestTestWithMissingDocumentUniqueId() {
    // Arrange & Act
    final var tstObj =
        assertDoesNotThrow(
            () ->
                RetrieveDocumentMessagesBuilder.buildFromRequest(
                    new RetrieveDocumentRequest(
                        null, null, null, null, "A", null, "B^PS.XML", "D")));

    // Assert
    assertEquals(1, tstObj.documentationInfos().size());
    final var doc = tstObj.documentationInfos().getFirst();
    assertEquals("B^PS.XML", doc.documentUniqueId());
    assertEquals("D", doc.homeCommunityId());
    assertEquals("A", doc.repositoryUniqueId());
    assertNull(tstObj.patient());
  }

  @Test
  void buildFromRequestAndPatientWithPatientTest() {
    // Arrange & Act
    final var tstObj =
        assertDoesNotThrow(
            () -> RetrieveDocumentMessagesBuilder.buildFromRequestAndPatient(testdata, PATIENT));

    // Assert
    assertEquals(2, tstObj.documentationInfos().size());
    final var doc1 = tstObj.documentationInfos().getFirst();
    assertEquals(testdata.documentUniqueId(), doc1.documentUniqueId());
    assertEquals(testdata.homeCommunityId(), doc1.homeCommunityId());
    assertEquals(testdata.repositoryUniqueId(), doc1.repositoryUniqueId());
    final var doc2 = tstObj.documentationInfos().get(1);
    assertEquals(testdata.additionalDocumentUniqueId(), doc2.documentUniqueId());
    assertEquals(testdata.homeCommunityId(), doc2.homeCommunityId());
    assertEquals(testdata.repositoryUniqueId(), doc2.repositoryUniqueId());
    assertEquals(PATIENT, tstObj.patient());
  }

  @Test
  void buildFromRequestAndPatientWithPatientTest2() {
    // Arrange & Act
    final var tstObj =
        assertDoesNotThrow(
            () -> RetrieveDocumentMessagesBuilder.buildFromRequestAndPatient(testdata2, PATIENT));
    final var docExpected = testdata2.documentRequestSet().stream().findFirst().orElseThrow();

    // Assert
    assertEquals(1, tstObj.documentationInfos().size());
    final var doc1 = tstObj.documentationInfos().getFirst();
    assertEquals(docExpected.documentUniqueId(), doc1.documentUniqueId());
    assertEquals(docExpected.homeCommunityId(), doc1.homeCommunityId());
    assertEquals(docExpected.repositoryUniqueId(), doc1.repositoryUniqueId());
    assertEquals(PATIENT, tstObj.patient());
  }

  @Test
  void buildRequestTest() {
    // Arrange
    final var tstObj = RetrieveDocumentMessagesBuilder.buildFromRequest(testdata);

    // Act
    final var result = assertDoesNotThrow(tstObj::buildRequest);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getDocumentRequest().size());
    final var docRequests = result.getDocumentRequest();
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

  @SneakyThrows
  @Test
  void buildResponseTest() {
    // Arrange
    final var tstObj =
        RetrieveDocumentMessagesBuilder.buildFromRequestAndPatient(testdata, PATIENT);

    // Act
    final var result = assertDoesNotThrow(tstObj::buildResponse);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getDocumentResponse().size());
    final var docResponses = result.getDocumentResponse();
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

    final Path pdfFile = Files.createTempFile("patientSummary", ".pdf");
    final Path xmlFile = Files.createTempFile("patientSummary", ".xml");

    docResponses.stream()
        .filter(dr -> dr.getDocumentUniqueId().endsWith(".PDF"))
        .findFirst()
        .ifPresent(dr -> assertDoesNotThrow(() -> Files.write(pdfFile, dr.getDocument())));

    docResponses.stream()
        .filter(dr -> dr.getDocumentUniqueId().endsWith(".XML"))
        .findFirst()
        .ifPresent(dr -> assertDoesNotThrow(() -> Files.write(xmlFile, dr.getDocument())));

    // compare xml
    final var expectedData = readResourceFile(getClass(), "Patient_Summary_CDA3.xml");
    assertThat(
        Files.readString(xmlFile, StandardCharsets.UTF_8),
        CompareMatcher.isSimilarTo(expectedData).ignoreWhitespace().ignoreComments());

    // compare pdf
    final byte[] testeePdf = Files.readAllBytes(pdfFile);
    final byte[] expectedPdf =
        Files.readAllBytes(
            Path.of(
                Objects.requireNonNull(this.getClass().getResource("Patient_Summary_CDA1.pdf"))
                    .toURI()));

    assertArrayEquals(expectedPdf, testeePdf, "The pdfs are not equal.");
  }
}
