/*
 * Copyright (c) 2024-2025 gematik GmbH
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

package de.gematik.ncpeh.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.gematik.ncpeh.api.common.EuCountryCode;
import de.gematik.ncpeh.api.common.IdaAssertionProfile;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Properties die in allen Trigger-Operationen verwendet werden.")
public record RequestBase(
    @JsonProperty(required = true)
        @Schema(
            description =
                "EuCountryCode benennt ein Datenprofil in Form eines zweistelligen EU-Landescodes. "
                    + "Über das Datenprofil werden die Zertifikate und die homeCommunityId des NCPeHs des EU-Landes definiert, "
                    + "die der NCPeH-Simulator in einem auszuführenden Request nutzen soll."
                    + "Siehe auch gematik Spezifikation, gemSpec_NCPeH_FD#4.5.3.1 \"Daten für das Triggern von Anwendungsfällen\".")
        EuCountryCode euCountryCode,
    @JsonProperty(required = true)
        @Schema(
            description =
                "IdaAssertionProfile benennt ein Datenprofil, dass zur Bildung der IdA-Assertion "
                    + "für einen Request an den NCPeH-Fachdienst zu nutzen ist. "
                    + "Es benennt mindestens das zu nutzende Profil (im Sinne eines Templates) und "
                    + "kann optional Abweichungen zum Profil für die Bildung der IdA-Assertion festlegen.")
        IdaAssertionProfile idaAssertionProfile) {}
