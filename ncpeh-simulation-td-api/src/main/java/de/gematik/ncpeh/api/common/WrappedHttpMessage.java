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
            type = "string")
        byte[] httpHeader,
    @JsonProperty
        @Schema(
            description = "Base64 kodierter ByteStream des HTTP body",
            nullable = true,
            format = "byte",
            type = "string")
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
