/*
 * Copyright (c) 2025 gematik GmbH
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
 */

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        "Daten zu Verordnungen, die der NCPeH-Simulator zur Generierung von DispenseDocuments benötigt.")
public record PrescriptionProfile(
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der prescriptionProfileName benennt ein Datenprofil (Template), "
                    + "das im NCPeH Simulator verfügbar sein muss. "
                    + "Über dieses Profil werden verschiedene benötigte Daten referenziert, "
                    + "die der NCPeH-Simulator bei der Generierung der DispenseDocuments nutzen soll.",
            example = "defaultPrescriptionProfile",
            maxLength = 30)
        String profileName,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Referenziert die Verordnung, für die Dispensierdaten übermittelt werden sollen.",
            example = "160.000.000.000.123.76^eP.XML",
            maxLength = 30)
        String prescriptionId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Gibt an, ob eine Substitution des verordneten Arzneimittels erlaubt ist.",
            example = "false")
        Boolean substitution) {}
