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
import de.gematik.ncpeh.api.common.PatientId;
import de.gematik.ncpeh.api.common.SubjectId;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

public record IdentifyPatientRequest(
    @JsonProperty(required = true) RequestBase baseParameter,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Gibt die Werte an, mit denen im XCPD-Request in der queryByParameter Liste im Element "
                    + "'PRPA_IN201305UV02/controlActProcess/queryByParameter/parameterList/livingSubjectID/value' "
                    + "die Attribute 'extension' und 'root' befüllt werden müssen. "
                    + "Definiert außerdem die Werte zur Berechnung der subjectId, die per Default in der TRC-Assertion genutzt werden sollen")
        PatientId patientId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der Wert repräsentiert den Zugriffscode, den der Versicherte dem LE-EU vor Ort übergibt. "
                    + "Aktuell ist es ein 6stelliger alphanumerischer Code: [A-Za-z0-9]{6}. "
                    + "Er ist in das Attribut \"extension\" eines Elementes PRPA_IN201305UV02/controlActProcess/queryByParameter/parameterList/livingSubjectID/value einzusetzen.",
            maxLength = 20)
        String accessCode,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der Wert repräsentiert die OID der Assigning Authority zum AccessCode. "
                    + "Er ist in das Attribut \"root\" des Elementes PRPA_IN201305UV02/controlActProcess/queryByParameter/parameterList/livingSubjectID/value einzusetzen. "
                    + "Spezifizierte Werte sind: oid der AssigningAuthority für das Szenario Patient Summary Land A = 1.2.276.0.76.4.298 und "
                    + "oid der AssigningAuthority für das Szenario ePrescription/eDispensation Land A = 1.2.276.0.76.4.299. "
                    + "Andere Werte können für Fehlerszenarien eingesetzt werden.",
            maxLength = 50)
        String accessCodeAssigningAuthority,
    @JsonProperty(defaultValue = "1.2.276.0.76.4.291")
        @Schema(
            defaultValue = "1.2.276.0.76.4.291",
            description =
                "Der übergebene String ist an den Stellen des XCPD-Requests einzutragen, "
                    + "an denen die homeCommunityId des deutschen NCPeH-Fachdienstes zum Einsatz kommen soll",
            maxLength = 50)
        String hcidNcpCountryA,
    @JsonProperty
        @Schema(
            description =
                "Der übergebene String ist an den Stellen des XCPD-Requests einzutragen, "
                    + "an denen die homeCommunityId des EU-Landes zum Einsatz kommt. "
                    + "Wird hier kein Wert übergeben, ist der Wert aus dem Datenprofil zu nutzen, "
                    + "dass über die Angabe des EuCountryCodes in der RequestBase referenziert wird.",
            maxLength = 50)
        String hcidNcpCountryB,
    @JsonProperty
        @Schema(
            description =
                "Wenn hier ein oder mehrere Wertpaare angegeben werden, "
                    + "so ist pro Wertepaar ein Listenelement 'value' im Element "
                    + "'PRPA_IN201305UV02/controlActProcess/queryByParameter/parameterList/livingSubjectID' einzufügen"
                    + "und mit den angegebenen Werten zu befüllen. "
                    + "Dies geschieht zusätzlich zu den value-Einträgen für die KVNR und den AccessCode in der livingSubjectID.")
        Set<SubjectId> additionalLivingSubjectId,
    @JsonProperty
        @Schema(
            description =
                "Der Wert ist in einem queryByParameter für patientAddress mit dem Unterelement country zu verwenden. Andere mögliche Elemente von patientAddress sind nicht zu nutzen."
                    + "PRPA_IN201305UV02/controlActProcess/queryByParameter/parameterList/patientAddress/value/country"
                    + "Wenn der Wert fehlt oder leer ist, dann wird er ignoriert und es wird kein Element patientAddress angelegt.")
        EuCountryCode patientAddressCountry) {}
