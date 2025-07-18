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

public record Slot(
    @JsonProperty(required = true)
        @Schema(description = "Bestimmt ein Slot-Element anhand seines Attributwertes \"name\".")
        SlotName name,
    @JsonProperty(required = true)
        @Schema(
            description = "Bestimmt den zu setzenden Wert in der ValueList des benannten Slots.",
            maxLength = 100,
            example = "2.999.1.2.3.1375^Welby^Marcus^^MD^Dr")
        String value) {}
