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

package de.gematik.ncpeh.api.mock.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.ncpeh.ehdsi.valuesets.EhdsiSubstitutionCode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.hl7.v3.ClinicalDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PrescriptionCda3UtilsTest {

  private static final String MEDICATION_NAME = "Metoprololsuccinat AL 47,5mg Retardtabletten";
  private static final String PATIENT_DATE_OF_BIRTH = "19670630";
  private static final String PATIENT_FIRST_NAME = "Maude Adelheid Lilo Johanna";
  private static final String PATIENT_LAST_NAME = "GõdofskýTEST-ONLY";
  private static final String PRESCRIPTION_UID = "1.2.276.0.76.4.299^160.000.000.000.123.76|eP.XML";
  private static final String PZN = "07097020";

  private static byte[] documentBytes;

  private ClinicalDocument clinicalDocument;

  @BeforeAll
  static void setupAll() {
    try (var in = PrescriptionCda3UtilsTest.class.getResourceAsStream("Prescription_CDA3.xml")) {
      documentBytes = in.readAllBytes();
    } catch (final IOException e) {
      throw new RuntimeException("could not read XML from file", e);
    }
  }

  @BeforeEach
  void setup() {
    clinicalDocument = XmlUtils.unmarshal(ClinicalDocument.class, documentBytes);
  }

  @Test
  void getPatientFirstName_shouldReturnFirstName() {
    assertEquals(PATIENT_FIRST_NAME, PrescriptionCda3Utils.getPatientFirstName(clinicalDocument));
  }

  @Test
  void getPatientLastName_shouldReturnLastName() {
    assertEquals(PATIENT_LAST_NAME, PrescriptionCda3Utils.getPatientLastName(clinicalDocument));
  }

  @Test
  void getPatientDateOfBirth_shouldReturnDateOfBirth() {
    assertEquals(
        LocalDate.parse(PATIENT_DATE_OF_BIRTH, DateTimeFormatter.BASIC_ISO_DATE),
        PrescriptionCda3Utils.getPatientDateOfBirth(clinicalDocument));
  }

  @Test
  void getSubstitutionValue_shouldReturnSubstitutionValue() {
    var substitutionValue = PrescriptionCda3Utils.getSubstitutionValue(clinicalDocument);
    assertEquals(EhdsiSubstitutionCode.GENERIC.getCode(), substitutionValue.getCode());
    assertEquals(
        EhdsiSubstitutionCode.GENERIC.getDisplayName(), substitutionValue.getDisplayName());
    assertEquals(EhdsiSubstitutionCode.CODE_SYSTEM_OID, substitutionValue.getCodeSystem());
    assertEquals(EhdsiSubstitutionCode.CODE_SYSTEM_NAME, substitutionValue.getCodeSystemName());
  }

  @Test
  void setSubstitutionValue_shouldModifySubstitutionValue() {
    var noSubstitution = EhdsiSubstitutionCode.NONE;
    PrescriptionCda3Utils.setSubstitutionValue(clinicalDocument, noSubstitution);
    var newValue = PrescriptionCda3Utils.getSubstitutionValue(clinicalDocument);
    assertEquals(noSubstitution.getCode(), newValue.getCode());
    assertEquals(noSubstitution.getDisplayName(), newValue.getDisplayName());
    assertEquals(EhdsiSubstitutionCode.CODE_SYSTEM_OID, newValue.getCodeSystem());
    assertEquals(EhdsiSubstitutionCode.CODE_SYSTEM_NAME, newValue.getCodeSystemName());
  }

  @Test
  void setPatientFirstName_shouldModifyFirstName() {
    var newName = "Mohandas Karamchand";
    assertNotEquals(newName, PrescriptionCda3Utils.getPatientFirstName(clinicalDocument));
    PrescriptionCda3Utils.setPatientFirstName(clinicalDocument, newName);
    assertEquals(newName, PrescriptionCda3Utils.getPatientFirstName(clinicalDocument));
  }

  @Test
  void setPatientLastName_shouldModifyLastName() {
    var newName = "Siebold";
    assertNotEquals(newName, PrescriptionCda3Utils.getPatientFirstName(clinicalDocument));
    PrescriptionCda3Utils.setPatientLastName(clinicalDocument, newName);
    assertEquals(newName, PrescriptionCda3Utils.getPatientLastName(clinicalDocument));
  }

  @Test
  void setPatientDateOfBirth_shouldModifyDateOfBirth() {
    var newDate = LocalDate.of(2026, 1, 1);
    assertNotEquals(newDate, PrescriptionCda3Utils.getPatientDateOfBirth(clinicalDocument));
    PrescriptionCda3Utils.setPatientDateOfBirth(clinicalDocument, newDate);
    assertEquals(newDate, PrescriptionCda3Utils.getPatientDateOfBirth(clinicalDocument));
  }

  @Test
  void getDocumentUid_shouldReturnDocumentUid() {
    assertEquals(PRESCRIPTION_UID, PrescriptionCda3Utils.getDocumentUid(clinicalDocument));
  }

  @Test
  void setDocumentUid_shouldModifyDocumentUid() {
    var newUid = "aNewUid";
    assertNotEquals(newUid, PrescriptionCda3Utils.getDocumentUid(clinicalDocument));
    PrescriptionCda3Utils.setDocumentUid(clinicalDocument, newUid);
    assertEquals(newUid, PrescriptionCda3Utils.getDocumentUid(clinicalDocument));
  }

  @ParameterizedTest
  @CsvSource({
    "1.2.276.0.76.4.299^160.000.000.000.123.76|eP.PDF, 1.2.276.0.76.4.299^160.000.000.000.123.76",
    "1.2.276.0.76.4.299^160.000.000.000.123.76|eP.XML, 1.2.276.0.76.4.299^160.000.000.000.123.76",
    "160.000.000.000.123.76^PS.PDF, 160.000.000.000.123.76",
    "160.000.000.000.123.76^PS.XML, 160.000.000.000.123.76"
  })
  void stripLevelMarkerFromDocumentUid_shouldStripMarkerCorrectly(
      final String documentUid, final String expectedResult) {
    // Act
    var stripped = PrescriptionCda3Utils.stripLevelMarkerFromDocumentUid(documentUid);

    // Assert
    assertEquals(expectedResult, stripped);
  }

  @Test
  void stripLevelMarkerFromDocumentUid_shouldThrowWhenNoMarkerPresent() {
    // Arrange
    var docUidNoMarker = "1.2.276.0.76.4.299^160.000.000.000.123.76";

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> PrescriptionCda3Utils.stripLevelMarkerFromDocumentUid(docUidNoMarker));
  }

  @Test
  void stripLevelMarkerFromDocumentUid_shouldThrowOnInvalidMarkerFormat() {
    // Arrange
    var docUidInvalidMarker = "1.2.276.0.76.4.299^160.000.000.000.123.76|INVALID";

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> PrescriptionCda3Utils.stripLevelMarkerFromDocumentUid(docUidInvalidMarker));
  }

  @ParameterizedTest
  @CsvSource({
    "1.2.276.0.76.4.299^160.000.000.000.123.76|eP.PDF, 160.000.000.000.123.76",
    "1.2.276.0.76.4.299^160.000.000.000.123.76|eP.XML, 160.000.000.000.123.76"
  })
  void extractPrescriptionIdFromDocumentUid_shouldExtractPrescriptionIdCorrectly(
      final String documentUid, final String expectedId) {
    assertEquals(
        expectedId, PrescriptionCda3Utils.extractPrescriptionIdFromDocumentUid(documentUid));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"1.2.276.0.76.4.299^160.000.000.000.123.76", "1.2.276.0.76.4.299^|eP.XML"})
  void extractPrescriptionIdFromDocumentUid_shouldThrowOnInvalidUidFormat(final String invalidUid) {
    assertThrows(
        IllegalArgumentException.class,
        () -> PrescriptionCda3Utils.extractPrescriptionIdFromDocumentUid(invalidUid));
  }

  @Test
  void getPzn_shouldReturnPzn() {
    assertEquals(PZN, PrescriptionCda3Utils.getPzn(clinicalDocument));
  }

  @Test
  void setPzn_shouldModifyPzn() {
    var newPzn = "12345678";
    assertNotEquals(newPzn, PrescriptionCda3Utils.getPzn(clinicalDocument));
    PrescriptionCda3Utils.setPzn(clinicalDocument, newPzn);
    assertEquals(newPzn, PrescriptionCda3Utils.getPzn(clinicalDocument));
  }

  @Test
  void getPznDisplayName_shouldReturnPznDisplayName() {
    assertEquals(MEDICATION_NAME, PrescriptionCda3Utils.getPznDisplayName(clinicalDocument));
  }

  @Test
  void setPznDisplayName_shouldModifyPznDisplayName() {
    var newDisplayName = "Neues Medikament 123";
    assertNotEquals(newDisplayName, PrescriptionCda3Utils.getPznDisplayName(clinicalDocument));
    PrescriptionCda3Utils.setPznDisplayName(clinicalDocument, newDisplayName);
    assertEquals(newDisplayName, PrescriptionCda3Utils.getPznDisplayName(clinicalDocument));
  }

  @Test
  void getMedicationName_shouldReturnMedicationName() {
    assertEquals(MEDICATION_NAME, PrescriptionCda3Utils.getMedicationName(clinicalDocument));
  }

  @Test
  void setMedicationName_shouldModifyMedicationName() {
    var newName = "Neues Medikament 123";
    assertNotEquals(newName, PrescriptionCda3Utils.getMedicationName(clinicalDocument));
    PrescriptionCda3Utils.setMedicationName(clinicalDocument, newName);
    assertEquals(newName, PrescriptionCda3Utils.getMedicationName(clinicalDocument));
  }
}
