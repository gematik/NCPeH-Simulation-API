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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Permission codes for eHDSI use cases as defined in the context of HL7's role engineering.
 *
 * @see <a
 *     href="https://webgate.ec.europa.eu/fpfis/wikis/spaces/EHDSI/pages/888398849/SAML+Profile">eHDSI
 *     SAML Profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum EhdsiXspaPermission {
  POE_006("POE-006", "Change/Discontinue/Refill Outpatient Prescription Order"),
  PRD_003("PRD-003", "Review Medical History"),
  PRD_004("PRD-004", "Review Existing Orders"),
  PRD_005("PRD-005", "Review Vital Signs/Patient Measurements"),
  PRD_006("PRD-006", "Patient Identification and Lookup"),
  PRD_010("PRD-010", "Review Patient Medications"),
  PRD_016("PRD-016", "Review Problem List"),
  PPD_029("PPD-029", "New Discharge Summary"),
  PPD_032("PPD-032", "New Consents and Authorizations"),
  PPD_033("PPD-033", "Edit/Addend/Sign Consents and Authorizations"),
  PPD_046("PPD-046", "Record Medication Administration Record");

  private static final String URN_PREFIX = "urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:";

  private final String code;
  private final String description;

  public String getUrn() {
    return URN_PREFIX + code;
  }
}
