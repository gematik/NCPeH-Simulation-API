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

package de.gematik.ncpeh.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        "Enthält die Informationen, die zur eindeutigen Identifikation eines "
            + "abzurufenden Dokuments benötigt werden und repräsentiert ein "
            + "'urn:ihe:iti:xds-b:2007:DocumentRequest' für die Cross Gateway Retrieve [ITI-39] "
            + "Transaktion.")
public record DocumentRequest(
    @JsonProperty(required = true)
        @Schema(
            example = "urn:oid:1.2.276.0.76.4.291",
            description =
                "Der angegebene Wert wird im RetrieveDocumentSetRequest dem Element "
                    + "'RetrieveDocumentSetRequest/DocumentRequest/HomeCommunityId' zugewiesen.")
        String homeCommunityId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der angegebene Wert wird im RetrieveDocumentSetRequest dem Element "
                    + "'RetrieveDocumentSetRequest/DocumentRequest/RepositoryUniqueId' zugewiesen.",
            example = "2.16.620.1.101.10.3.29.54290")
        String repositoryUniqueId,
    @JsonProperty(required = true)
        @Schema(
            description =
                "Der angegebene Wert wird im RetrieveDocumentSetRequest dem Element "
                    + "'RetrieveDocumentSetRequest/DocumentRequest/DocumentUniqueId' zugewiesen. "
                    + "Der Parameter wird vollständig konstruiert übergeben (also UniqueId + '^' + Formattyp).",
            example = "2.16.17.710.813.1000.990.1.1^PS.XML")
        String documentUniqueId) {}
