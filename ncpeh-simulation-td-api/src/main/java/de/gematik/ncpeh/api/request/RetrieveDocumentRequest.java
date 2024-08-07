/*
 * Copyright 2023 gematik GmbH
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
import io.swagger.v3.oas.annotations.media.Schema;

public record RetrieveDocumentRequest(
    @JsonProperty(required = true) RequestBase baseParameter,
    @JsonProperty(required = true)
        @Schema(
            description =
                "TrcAssertionProfile benennt ein Datenprofil, dass zur Bildung der TRC-Assertion "
                    + "für einen RetrieveDocumentRequest an den NCPeH-Fachdienst zu nutzen ist. "
                    + "Es benennt mindestens das zu nutzende Profil (im Sinne eines Templates) und "
                    + "kann optional Abweichungen zum benannten Profil für die Bildung der TRC-Assertion festlegen.")
        TrcAssertionProfile trcAssertionProfile,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Definiert die Werte zur Berechnung der subjectId, die per Default in der TRC-Assertion genutzt werden sollen.")
        PatientId patientId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der Wert repräsentiert den Zugriffscode, den der Versicherte dem LE-EU vor Ort übergibt. "
                    + "Aktuell ist es ein 6stelliger alphanumerischer Code: [A-Za-z0-9]{6}. "
                    + "Sofern im Parameter \"PatientId\" zur TRC-Assertion nicht anders angegeben, geht dieser Wert auch dort als AccessCode in den Bildungsschritt des Elementes \"urn:oasis:names:tc:xspa:1.0:subject:subject-id\" mit ein.",
            maxLength = 20)
        String accessCode,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Mit dem angegebenen Wert wird im RetrieveDocumentSetRequest der Wert "
                    + "des Elementes 'RetrieveDocumentSetRequest/DocumentRequest/RepositoryUniqueId' befüllt.")
        String repositoryUniqueId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Mit dem angegebenen Wert wird im RetrieveDocumentSetRequest der Wert "
                    + "des Elementes 'RetrieveDocumentSetRequest/DocumentRequest/DocumentUniqueId' befüllt. "
                    + "Der Parameter wird vollständig konstruiert übergeben (also UniqueId + '^' + Formattyp).")
        String documentUniqueId,
    @JsonProperty
        @Schema(
            description =
                "Es gelten die gleichen fachlichen Vorgaben wie zum Parameter documentUniqueId."
                    + "Der Parameter ist zu nutzen, wenn beide Ausprägungen des Patient Summaries in einem Request abgerufen werden sollen (CDA L1 & CDA L3)")
        String additionalDocumentUniqueId,
    @JsonProperty(defaultValue = "urn:oid:1.2.276.0.76.4.291")
        @Schema(
            defaultValue = "urn:oid:1.2.276.0.76.4.291",
            description =
                "Mit dem angegebenen Wert wird im RetrieveDocumentSetRequest der Wert "
                    + "des Elementes 'RetrieveDocumentSetRequest/DocumentRequest/HomeCommunityId' befüllt.")
        String homeCommunityId) {}
