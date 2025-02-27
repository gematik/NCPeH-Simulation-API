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
import de.gematik.ncpeh.api.common.PatientId;
import de.gematik.ncpeh.api.common.TrcAssertionProfile;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

public record RetrieveSetOfDocumentsRequest(
    @JsonProperty(required = true) RequestBase baseParameter,
    @JsonProperty(required = true)
        @Schema(
            description =
                "TrcAssertionProfile benennt ein Datenprofil, das zur Bildung der TRC-Assertion "
                    + "für einen RetrieveDocumentRequest an den NCPeH-Fachdienst zu nutzen ist. "
                    + "Es benennt mindestens das zu nutzende Profil (im Sinne eines Templates) und "
                    + "kann optional Abweichungen zum benannten Profil für die Bildung der TRC-Assertion festlegen.")
        TrcAssertionProfile trcAssertionProfile,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Definiert die Werte zur Berechnung der subjectId, die standardmäßig in der TRC-Assertion genutzt "
                    + "werden sollen.")
        PatientId patientId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der Wert repräsentiert den Zugriffscode, den die versicherte Person dem LE-EU vor Ort übergibt. "
                    + "Aktuell ist es ein 6-stelliger alphanumerischer Code: [A-Za-z0-9]{6}. "
                    + "Sofern im Parameter \"patientId\" zur TRC-Assertion nicht anders angegeben, geht dieser Wert "
                    + "auch dort als AccessCode in den Bildungsschritt des Elementes "
                    + "\"urn:oasis:names:tc:xspa:1.0:subject:subject-id\" mit ein.",
            maxLength = 20,
            example = "ABC123")
        String accessCode,
    @JsonProperty(required = true)
        @ArraySchema(
            arraySchema = @Schema(description = "Set der Dokumente, die abgerufen werden sollen."))
        Set<DocumentRequest> documentRequestSet) {}
