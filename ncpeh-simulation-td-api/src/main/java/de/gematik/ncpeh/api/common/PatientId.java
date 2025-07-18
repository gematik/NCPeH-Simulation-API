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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        "Informationen zur eindeutigen Identifizierung eines Patienten in der deutschen TI.")
public record PatientId(
    @JsonProperty(required = true)
        @Schema(
            description = "Der Wert repräsentiert die KVNR des Versicherten",
            maxLength = 20,
            example = "X123456789")
        String kvnr,
    @JsonProperty(defaultValue = "1.2.276.0.76.3.1.580.147")
        @Schema(
            defaultValue = "1.2.276.0.76.3.1.580.147",
            description = "Der Wert repräsentiert die OID der Assigning Authority zur KVNR",
            maxLength = 50,
            example = "1.2.276.0.76.3.1.580.147")
        String kvnrAssigningAuthority) {}
