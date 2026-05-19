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
 * Enum representing the valueset for indicating a health professional's structural role as defined
 * by the eHDSI SAML Profile v9.0.0 (2025-07-07)
 *
 * @see <a
 *     href="https://webgate.ec.europa.eu/fpfis/wikis/pages/viewpage.action?pageId=888398849">eHDSI
 *     SAML Profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum EhdsiXspaRole {
  MEDICAL_DOCTORS(221, "Medical doctors"),
  NURSING_PROFESSIONALS(2221, "Nursing professionals"),
  MIDWIFERY_PROFESSIONALS(2222, "Midwifery professionals"),
  PHARMACISTS(2262, "Pharmacists"),
  PHARMACEUTICAL_TECHNICIANS_AND_ASSISTANTS(3213, "Pharmaceutical technicians and assistants"),
  DENTISTS(2261, "Dentists"),
  PHYSIOTHERAPISTS(2264, "Physiotherapists"),
  DIETICIANS_AND_NUTRITIONISTS(2265, "Dieticians and nutritionists"),
  AUDIOLOGISTS_AND_SPEECH_THERAPISTS(2266, "Audiologists and speech therapists"),
  OPTOMETRISTS_AND_OPHTHALMIC_OPTICIANS(2267, "Optometrists and ophthalmic opticians"),
  MEDICAL_IMAGING_AND_THERAPEUTIC_EQUIPMENT_TECHNICIANS(
      3211, "Medical imaging and therapeutic equipment technicians"),
  HEALTH_PROFESSIONALS_NOT_ELSEWHERE_CLASSIFIED(
      2269, "Health professionals not elsewhere classified"),
  OTHER_CLERICAL_SUPPORT_WORKERS(44, "Other Clerical Support Workers");

  public static final String CODE_SYSTEM_OID = "2.16.840.1.113883.2.9.6.2.7";
  public static final String CODE_SYSTEM_NAME = "ISCO";

  private final int code;
  private final String displayName;

  public String getCodeAsString() {
    return String.valueOf(code);
  }
}
