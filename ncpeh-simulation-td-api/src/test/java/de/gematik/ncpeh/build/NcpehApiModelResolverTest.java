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

package de.gematik.ncpeh.build;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.core.util.Yaml31;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.OpenApiContextLocator;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import org.junit.jupiter.api.Test;

class NcpehApiModelResolverTest {

  @Test
  void constructorWithObjMapper() {
    var modelResolver =
        assertDoesNotThrow(
            () -> new NcpehApiModelResolver(Yaml31.mapper()),
            "Constructor of NcpehApiModelResolver threw exception");
  }

  @Test
  void constructorWithoutObjMapper() {
    var modelResolver =
        assertDoesNotThrow(
            () -> new NcpehApiModelResolver(),
            "Constructor of NcpehApiModelResolver threw exception");

    OpenApiContextLocator.getInstance()
        .putOpenApiContext(
            OpenApiContext.OPENAPI_CONTEXT_ID_DEFAULT,
            new GenericOpenApiContext<>().outputYamlMapper(Yaml31.mapper()));

    modelResolver =
        assertDoesNotThrow(
            () -> new NcpehApiModelResolver(),
            "Constructor of NcpehApiModelResolver threw exception");
    assertEquals(Yaml31.mapper(), modelResolver.objectMapper());
  }

  @Test
  void constructorWithTypeNameResolver() {
    var modelResolver =
        assertDoesNotThrow(
            () -> new NcpehApiModelResolver(Yaml.mapper(), TypeNameResolver.std),
            "Constructor of NcpehApiModelResolver threw exception");
  }
}
