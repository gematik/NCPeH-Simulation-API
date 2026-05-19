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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        "Daten zu Verordnungen, die der NCPeH-Simulator zur Generierung von DispenseDocuments benötigt.")
public record PrescriptionDispenseData(
    @JsonProperty(required = true)
        @Schema(
            description =
                "Referenziert die Verordnung, für die Dispensierdaten übermittelt werden sollen.",
            example = "1.2.276.0.76.4.299^160.000.000.000.123.76|eP.XML")
        String prescriptionId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Gibt an, ob in den zu sendenden Dispensierinformationen angegeben werden soll, "
                    + "dass eine Substitution des verordneten Arzneimittels vorgenommen wurde.",
            example = "false")
        Boolean isSubstituted) {}
