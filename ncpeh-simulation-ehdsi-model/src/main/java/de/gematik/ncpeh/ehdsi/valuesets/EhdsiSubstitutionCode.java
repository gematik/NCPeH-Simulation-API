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
 * Enum representing substitution codes for substance administration in the eHDSI domain
 *
 * @see <a
 *     href="https://art-decor.ehdsi.eu/ad/#/epsos-/terminology/valueset/1.3.6.1.4.1.12559.11.10.1.3.1.42.7/2022-04-28T17:25:00">value
 *     set 'eHDSISubstitutionCode' (version 202204) on ART-DECOR</a>
 */
@Getter
@RequiredArgsConstructor
public enum EhdsiSubstitutionCode {
  GENERIC("G", "generic"),
  NONE("N", "none"),
  THERAPEUTIC("TE", "therapeutic");

  public static final String CODE_SYSTEM_OID = "2.16.840.1.113883.5.1070";
  public static final String CODE_SYSTEM_NAME = "Substance Admin Substitution";

  private final String code;
  private final String displayName;
}
