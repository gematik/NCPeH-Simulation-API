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
import java.util.Arrays;
import java.util.Objects;

public record WrappedHttpMessage(
    @JsonProperty(required = true)
        @Schema(
            description = "Base64 kodierter ByteStream aller HTTP header",
            format = "byte",
            type = "string",
            example =
                "W0FjY2VwdDoidGV4dC94bWwsIGFwcGxpY2F0aW9uL3NvYXAreG1sO2NoYXJzZXQ9VVRGLTgiLCBDb250ZW"
                    + "50LVR5cGU6ImFwcGxpY2F0aW9uL3NvYXAreG1sO2NoYXJzZXQ9VVRGLTgiXQ==")
        byte[] httpHeader,
    @JsonProperty
        @Schema(
            description = "Base64 kodierter ByteStream des HTTP body",
            nullable = true,
            format = "byte",
            type = "string",
            example =
                "PFNPQVAtRU5WOkVudmVsb3BlCgl4bWxuczpTT0FQLUVOVj0iaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcm"
                    + "cvc29hcC9lbnZlbG9wZS8iPgoJPFNPQVAtRU5WOkhlYWRlci8+Cgk8U09BUC1FTlY6Qm9keT4KCQ"
                    + "k8LS0gUGF5bG9hZCAtLT4KCTwvU09BUC1FTlY6Qm9keT4KPC9TT0FQLUVOVjpFbnZlbG9wZT4=")
        byte[] httpBody) {

  @Override
  public boolean equals(Object obj) {
    return Objects.nonNull(obj) && this.hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode() + Arrays.hashCode(httpHeader) + 7 * Arrays.hashCode(httpBody);
  }

  @Override
  public String toString() {
    return this.getClass() + ": " + Arrays.toString(httpHeader) + ", " + Arrays.toString(httpBody);
  }
}
