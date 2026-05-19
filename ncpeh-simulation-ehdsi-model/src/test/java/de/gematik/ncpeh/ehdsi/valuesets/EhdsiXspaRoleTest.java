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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EhdsiXspaRoleTest {

  @ParameterizedTest
  @CsvSource({
    "MEDICAL_DOCTORS, 221",
    "PHARMACISTS, 2262",
    "PHARMACEUTICAL_TECHNICIANS_AND_ASSISTANTS, 3213",
    "PHYSIOTHERAPISTS, 2264",
    "OTHER_CLERICAL_SUPPORT_WORKERS, 44"
  })
  void shouldReturnCodeAsString(final EhdsiXspaRole role, final String expected) {
    assertEquals(expected, role.getCodeAsString());
  }
}
