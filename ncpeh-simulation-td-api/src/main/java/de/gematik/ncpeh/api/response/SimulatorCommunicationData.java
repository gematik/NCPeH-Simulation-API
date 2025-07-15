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

package de.gematik.ncpeh.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.gematik.ncpeh.api.common.WrappedHttpRequest;
import de.gematik.ncpeh.api.common.WrappedHttpResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description =
        "Request und Response der Kommunikation zwischen NCPeH Simulator und NCPeH Fachdienst")
public record SimulatorCommunicationData(
    @JsonProperty(required = true)
        @Schema(
            description =
                "IHE Request, wie er vom NCPeH Simulator an den NCPeH Fachdienst versendet wurde.")
        WrappedHttpRequest requestSend,
    @JsonProperty(required = true)
        @Schema(
            description =
                "IHE Response, wie er vom NCPeH Simulator vom NCPeH Fachdienst empfangen wurde.")
        WrappedHttpResponse responseReceived) {}
