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

public record TrcAssertionProfile(
    @JsonProperty(required = true) BaseAssertionProfile assertionProperties,
    @JsonProperty
        @Schema(
            description =
                "Wenn hier ein Wert gesetzt ist, bestimmt dieser den vollständigen Inhalt im "
                    + "Assertion-Element 'Security/Assertion/AttributeStatement/Attribute/AttributeValue' "
                    + "in dem für das Element 'Attribute' 'Name=\"urn:oasis:names:tc:xspa:1.0:subject:subject-id\"' gilt. "
                    + "Falls die Angabe fehlt, so sind mit der KVNR und die OID der Assigning Authority zum AccessCode aus dem fachlichen Kontext "
                    + "des Trigger-Requests (Element patientId aus diesem Request) "
                    + "eine gültige URN für das SOAP-Element zu berechnen.",
            maxLength = 70)
        String patientId) {}
