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

import static de.gematik.ncpeh.api.mock.TestUtils.readResourceFile;
import static de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo.EP_LEVEL_1;
import static de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo.EP_LEVEL_3;
import static de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo.PS_LEVEL_1;
import static de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo.PS_LEVEL_3;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.ncpeh.api.mock.builder.RetrieveDocumentMessagesBuilder.CDALevelInfo;
import de.gematik.ncpeh.api.mock.data.Medication;
import de.gematik.ncpeh.api.mock.data.Patient;
import de.gematik.ncpeh.api.mock.data.PatientImpl;
import de.gematik.ncpeh.api.mock.data.PersonName;
import de.gematik.ncpeh.api.mock.util.PrescriptionCda3Utils;
import de.gematik.ncpeh.api.mock.util.XmlUtils;
import de.gematik.ncpeh.api.request.DocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveDocumentRequest;
import de.gematik.ncpeh.api.request.RetrieveSetOfDocumentsRequest;
import de.gematik.ncpeh.ehdsi.valuesets.EhdsiSubstitutionCode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.SneakyThrows;
import org.hl7.v3.ClinicalDocument;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.xmlunit.matchers.CompareMatcher;

class RetrieveDocumentMessagesBuilderTest {

  private final RetrieveDocumentRequest testdata =
      new RetrieveDocumentRequest(
          null,
          null,
          null,
          null,
          "A",
          "B" + PS_LEVEL_1.idMarker(),
          "B" + PS_LEVEL_3.idMarker(),
          "D");

  private final RetrieveSetOfDocumentsRequest testdata2 =
      new RetrieveSetOfDocumentsRequest(
          null, null, null, null, Set.of(new DocumentRequest("DD", "AA", "ID")));

  private final RetrieveSetOfDocumentsRequest prescriptionRequest =
      new RetrieveSetOfDocumentsRequest(
          null,
          null,
          null,
          null,
          Set.of(new DocumentRequest("hcid", "repoUid", "x^pid" + EP_LEVEL_3.idMarker())));

  private static final Patient PATIENT =
      new PatientImpl(
          new PersonName()
              .titles("Gräfin")
              .lastnames("GõdofskýTEST-ONLY")
              .givennames("Maude Adelheid Lilo Johanna"),
          LocalDate.of(1967, 6, 30),
          "testKvnr");

  @Test
  void buildFromRequestTest() {
    // Arrange & Act
    final var tstObj =
        assertDoesNotThrow(() -> RetrieveDocumentMessagesBuilder.fromRequest(testdata));

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
                RetrieveDocumentMessagesBuilder.fromRequest(
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
  void buildFromRequestWithPatientTest2() {
    // Arrange & Act
    final var tstObj =
        assertDoesNotThrow(
            () -> RetrieveDocumentMessagesBuilder.fromRequest(testdata2).patient(PATIENT));
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
    final var tstObj = RetrieveDocumentMessagesBuilder.fromRequest(testdata);

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

  @Test
  void buildResponse_shouldCreatePrescriptionIfRequested() {
    // Arrange
    var docRequest = prescriptionRequest.documentRequestSet().iterator().next();
    var meds = Map.of("pid", new Medication("dummyMed", "11111111", true));
    var tstObj =
        RetrieveDocumentMessagesBuilder.fromRequest(prescriptionRequest)
            .patient(PATIENT)
            .medicationByPrescriptionId(meds);
    var result = tstObj.buildResponse();

    assertNotNull(result);
    assertEquals(1, result.getDocumentResponse().size());

    var docResponse = result.getDocumentResponse().getFirst();
    assertEquals(docRequest.documentUniqueId(), docResponse.getDocumentUniqueId());
    assertEquals(docRequest.homeCommunityId(), docResponse.getHomeCommunityId());
    assertEquals(docRequest.repositoryUniqueId(), docResponse.getRepositoryUniqueId());
    assertNotNull(docResponse.getDocument());
  }

  @SneakyThrows
  @Test
  void epLevel1Creation_shouldThrowRuntimeExceptionWhenFileCannotBeRead() {
    assertThrows(RuntimeException.class, () -> EP_LEVEL_1.readOrCreateDocument(null, null, null));
  }

  @Test
  void epLevel1Creation_shouldCallPdfCreatorWithExpectedContent() {
    // Arrange
    var medication = new Medication("TestMed", "1", false);
    var pdfBuilderMock = Mockito.mock(PrescriptionPdfBuilder.class, Mockito.RETURNS_SELF);
    var dummyContent = "dummyPdfContent".getBytes(StandardCharsets.UTF_8);
    when(pdfBuilderMock.build()).thenReturn(dummyContent);

    try (var staticPdfBuilderMock = Mockito.mockStatic(PrescriptionPdfBuilder.class)) {
      when(PrescriptionPdfBuilder.newInstance()).thenReturn(pdfBuilderMock);

      // Act
      var documentBytes =
          EP_LEVEL_1.readOrCreateDocument(PATIENT, "abc^pid" + EP_LEVEL_1.idMarker(), medication);

      // Assert
      var document = XmlUtils.unmarshal(ClinicalDocument.class, documentBytes);
      var payload =
          document.getComponent().getNonXMLBody().getText().getContent().getFirst().trim();
      assertEquals(Base64.getEncoder().encodeToString(dummyContent), payload);
      verify(pdfBuilderMock).name(PATIENT.name().toString());
      verify(pdfBuilderMock)
          .birthdate(PATIENT.birthdate().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
      verify(pdfBuilderMock).kvnr(PATIENT.kvnr());
      verify(pdfBuilderMock).medications(List.of(medication));
    }
  }

  @Test
  void epLevel3Creation_shouldSetSubstitutionCodeWhenDisallowed() {
    // Arrange
    var medication = new Medication("TestMed", "1", false);
    try (var utilsMock = Mockito.mockStatic(PrescriptionCda3Utils.class)) {
      EP_LEVEL_3.readOrCreateDocument(PATIENT, "abc^pid" + EP_LEVEL_3.idMarker(), medication);
      utilsMock.verify(
          () ->
              PrescriptionCda3Utils.setSubstitutionValue(
                  isA(ClinicalDocument.class), same(EhdsiSubstitutionCode.NONE)));
    }
  }
}
