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

package de.gematik.ncpeh.ehdsi.valuesets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EhdsiXspaPermissionTest {

  @ParameterizedTest
  @CsvSource({
    "POE_006, POE-006",
    "PRD_003, PRD-003",
    "PRD_010, PRD-010",
    "PRD_016, PRD-016",
    "PPD_046, PPD-046"
  })
  void shouldReturnCorrectCodeForConstant(EhdsiXspaPermission permission, String expectedCode) {
    assertEquals(expectedCode, permission.getCode());
  }

  @ParameterizedTest
  @CsvSource({
    "PRD_004, Review Existing Orders",
    "PRD_005, Review Vital Signs/Patient Measurements",
    "PRD_006, Patient Identification and Lookup",
    "PPD_032, New Consents and Authorizations",
    "PPD_033, Edit/Addend/Sign Consents and Authorizations",
  })
  void shouldReturnDescriptionForConstant(
      EhdsiXspaPermission permission, String expectedDescription) {
    assertEquals(expectedDescription, permission.getDescription());
  }

  @ParameterizedTest
  @CsvSource({
    "POE_006, urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:POE-006",
    "PRD_003, urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-003",
    "PRD_010, urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-010",
    "PPD_029, urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-029",
    "PPD_046, urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PPD-046"
  })
  void shouldReturnUrnForConstant(EhdsiXspaPermission permission, String expectedUrn) {
    assertEquals(expectedUrn, permission.getUrn());
  }
}
