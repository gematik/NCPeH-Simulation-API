/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.ncpeh.util;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.jackson.mixin.SchemaMixin;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public class NcpehApiModelResolver extends ModelResolver {

  public NcpehApiModelResolver() {
    this(determineObjectMapper());
  }

  public NcpehApiModelResolver(ObjectMapper mapper) {
    super(mapper);
    modifyObjectMapper(mapper);
  }

  public NcpehApiModelResolver(ObjectMapper mapper, TypeNameResolver typeNameResolver) {
    super(mapper, typeNameResolver);
    modifyObjectMapper(mapper);
  }

  private void modifyObjectMapper(ObjectMapper mapper) {
    mapper.addMixIn(Schema.class, SchemaSortOrder.class);
  }

  private static ObjectMapper determineObjectMapper() {
    return Optional.ofNullable(
            OpenApiContextLocator.getInstance()
                .getOpenApiContext(OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT))
        .map(OpenApiContext::getOutputYamlMapper)
        .orElseGet(Yaml::mapper);
  }

  @JsonPropertyOrder({
    "title",
    "summary",
    "operationId",
    "type",
    "format",
    "uniqueItems",
    "default",
    "description"
  })
  private abstract static class SchemaSortOrder extends SchemaMixin {}
}
