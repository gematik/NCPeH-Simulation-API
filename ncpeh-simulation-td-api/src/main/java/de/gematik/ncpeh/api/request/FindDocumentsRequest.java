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
import de.gematik.ncpeh.api.common.Slot;
import de.gematik.ncpeh.api.common.TrcAssertionProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

public record FindDocumentsRequest(
    @JsonProperty(required = true) RequestBase baseParameter,
    @JsonProperty(required = true)
        @Schema(
            description =
                "TrcAssertionProfile benennt ein Datenprofil, dass zur Bildung der TRC-Assertion "
                    + "für einen FindDocumentsRequest an den NCPeH-Fachdienst zu nutzen ist. "
                    + "Es benennt mindestens das zu nutzende Profil (im Sinne eines Templates) und "
                    + "kann optional Abweichungen zum benannten Profil für die Bildung der TRC-Assertion festlegen.")
        TrcAssertionProfile trcAssertionProfile,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Gibt die Daten zur Berechnung des Wertes an, der im AdhocQueryRequest im "
                    + "Element 'AdhocQueryRequest/AdhocQuery/Slot/ValueList/Value' mit "
                    + "Slot 'name=\"$XDSDocumentEntryPatientId\"' eingetragen werden muss."
                    + "Definiert außerdem die Werte zur Berechnung der subjectId, die per Default in der TRC-Assertion genutzt werden sollen")
        PatientId patientId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der Wert repräsentiert den Zugriffscode, den der Versicherte dem LE-EU vor Ort übergibt. "
                    + "Aktuell ist es ein 6stelliger alphanumerischer Code: [A-Za-z0-9]{6}. "
                    + "Der Wert geht zusammen mit den Parametern KVNR und OID_KvnrAssigningAuthority in die Berechnung der XDSDocumentEntryPatientId entsprechend den Vorgaben der Spezifikation mit ein. "
                    + "Sofern im Parameter \"PatientId\" zur TRC-Assertion nicht anders angegeben, geht dieser Wert auch dort als AccessCode in den Bildungsschritt des Elementes \"urn:oasis:names:tc:xspa:1.0:subject:subject-id\" mit ein.",
            maxLength = 20)
        String accessCode,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Enthält den vollständigen Wert, der im AdhocQueryRequest im "
                    + "Element 'AdhocQueryRequest/AdhocQuery/Slot/ValueList/Value' mit "
                    + "Slot 'name=\"XDSDocumentEntryClassCode\"' eingetragen werden muss.",
            example = "('60591-5^^2.16.840.1.113883.6.1')",
            maxLength = 50)
        String xdsDocumentEntryClassCode,
    @JsonProperty(defaultValue = "('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')")
        @Schema(
            defaultValue = "('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved')",
            description =
                "Enthält den vollständigen Wert, der im AdhocQueryRequest im "
                    + "Element 'AdhocQueryRequest/AdhocQuery/Slot/ValueList/Value' mit "
                    + "Slot 'name=\"$XDSDocumentEntryStatus\"' eingetragen werden muss.")
        String xdsDocumentEntryStatus,
    @JsonProperty
        @Schema(
            description =
                "Werden ein oder mehrere Einträge angegeben, so sind diese als zusätzliche Slots "
                    + "im Element 'AdhocQueryRequest/AdhocQuery' einzutragen. "
                    + "Nur ein Eintrag pro Slot-Namen ist zulässig. "
                    + "Sobald die API-Beschreibung auf den OpenApi 3.1 Standard angehoben wurde, "
                    + "wird dies zu einer Liste von Properties mit einem Enum für die zulässigen Propertynamen geändert")
        Set<Slot> slotEntries) {}
