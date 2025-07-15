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
import java.util.Set;

public record IdaAssertionProfile(
    @JsonProperty(required = true) BaseAssertionProfile assertionProperties,
    @JsonProperty
        @Schema(
            description =
                "Wenn der Wert fehlt oder leer ist, dann wird er ignoriert und die im angegebenen AssertionProfil "
                    + "definierte Rolle genutzt.\n"
                    + "Andernfalls ersetzt der hier angegebene Wert das Datum 'AttributeValue/Role@code' für das "
                    + "Assertion-Element mit 'Name=\"urn:oasis:names:tc:xacml:2.0:subject:role\"'.",
            example = "221")
        String structuralRole,
    @JsonProperty
        @Schema(
            description =
                "Wenn die StringListe fehlt oder leer ist, dann MÜSSEN die mit dem angegebenen AssertionProfil "
                    + "definierten permissions-Einträge genutzt werden."
                    + "Andernfalls ersetzen die hier übergebenen Werte vollständig die Liste der "
                    + "Assertion-Elemente 'Security/Assertion/AttributeStatement/Attribute/AttributeValue' "
                    + "für die im Element 'Attribute' 'Name=\"urn:oasis:names:tc:xspa:1.0:subject:hl7:permission\"' gilt. "
                    + "Für jeden Wert aus der StringListe ist in ein eigenes Element 'AttributeValue' in der Assertion zu erstellen.",
            example =
                "[\"urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-004\", \"urn:oasis:names:tc:xspa:1.0:subject:hl7:permission:PRD-010\"]")
        Set<String> permissions,
    @JsonProperty
        @Schema(
            description =
                "Die Liste enthält zu setzende Werte für die Assertion-Elemente 'Security/Assertion/AttributeStatement/Attribute/AttributeValue' \n"
                    + "Wenn ein zulässiger Wert hier fehlt, dann wird - falls definiert - der im AssertionProfil festgelegte Wert genutzt. "
                    + "Ist er sowohl hier als auch im AssertionProfil nicht definiert, so muss er in der zu bildenden Assertion fehlen. "
                    + "Ist ein Wert sowohl im AssertionProfil als auch hier definiert, dann ist der hier angegebene Wert "
                    + "zu nutzen. (der Wert im Profil wird überschrieben)"
                    + "Nur ein Eintrag pro Namenswert ist in der Assertion zulässig. "
                    + "Sobald die API-Beschreibung auf den OpenApi 3.1 Standard angehoben wurde, "
                    + "wird dies zu einer Liste von Properties mit einem Enum für die zulässigen Propertynamen geändert.")
        Set<Saml2AttributeProperty> attributeStatements) {}
