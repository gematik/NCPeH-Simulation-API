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

package de.gematik.ncpeh.api.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record BaseAssertionProfile(
    @JsonProperty(required = true)
        @Schema(
            maxLength = 20,
            description =
                "Der profileName im BaseAssertionProfile benennt ein Datenprofil (Template), "
                    + "das im NCPeH Simulator verfügbar sein muss. "
                    + "Über das BaseAssertionProfile werden verschiedene benötigte Daten referenziert, "
                    + "die der NCPeH-Simulator in einem auszuführenden Request an den NCPeH-Fachdienst sowohl im "
                    + "IdaAssertionProfile als auch im TrcAssertionProfile nutzen soll."
                    + "Siehe auch gematik Spezifikation, gemSpec_NCPeH_FD#4.5.3.1 \"Daten für das Triggern von Anwendungsfällen\".")
        String profileName,
    @JsonProperty(defaultValue = "false")
        @Schema(
            defaultValue = "false",
            description =
                "Wenn false, MUSS eine normale, gültige Signatur der Assertion erstellt werden. "
                    + "Wenn true, MUSS eine Signatur durchgeführt und nachträglich der Wert des "
                    + "Elementes 'Header/Security/Signature/SignedInfo/Reference/DigestValue' verfälscht werden.")
        Boolean invalidSignature,
    @JsonProperty(defaultValue = "-30")
        @Schema(
            defaultValue = "-30",
            description =
                "Wertangabe in Minuten, der Wert kann positiv oder negativ sein. "
                    + "Im SOAP-Element 'Header/Security/Assertion/Conditions' wird der Zeitstempel im Attribut "
                    + "'NotBefore' wie folgt berechnet: Systemzeit des Simulators in UTC + deltaNotBefore")
        Integer deltaNotBefore,
    @JsonProperty(defaultValue = "30")
        @Schema(
            defaultValue = "30",
            description =
                "Wertangabe in Minuten, der Wert kann positiv oder negativ sein. "
                    + "Im SOAP-Element 'Header/Security/Assertion/Conditions' wird der Zeitstempel im Attribut "
                    + "'NotOnOrAfter' wie folgt berechnet: Systemzeit des Simulators in UTC + deltaNotAfter")
        Integer deltaNotAfter,
    @JsonProperty(defaultValue = "TREATMENT")
        @Schema(
            defaultValue = "TREATMENT",
            description =
                "Der Parameter bestimmt den Wert im SOAP-Element 'Header/Security/Assertion/AttributeStatement/Attribute/AttributeValue' "
                    + "in dem für das Element 'Attribute' 'Name=\"urn:oasis:names:tc:xspa:1.0:subject:purposeofuse\"' gilt.",
            maxLength = 20)
        String purposeOfUse) {}
