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

package de.gematik.ncpeh.api.mock.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class PatientImpl implements Patient {

  @JsonProperty private PersonName name;

  @JsonProperty private LocalDate birthdate;

  @JsonProperty private String kvnr;

  @JsonProperty private String accessCode;

  public PatientImpl(final PersonName name, final LocalDate birthDate) {
    this(name, birthDate, null, null);
  }

  public PatientImpl(final PersonName name, final LocalDate birthDate, final String kvnr) {
    this(name, birthDate, kvnr, null);
  }

  @Override
  public String toString() {
    return String.format(
        "Name: %s; KVNR: %s; Access Code: %s; Birth Date: %s", name, kvnr, accessCode, birthdate);
  }
}
