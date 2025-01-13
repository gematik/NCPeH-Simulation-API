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

package de.gematik.ncpeh.api.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.SneakyThrows;

/** Utility class for loading JSON resources. */
public class TestUtils {

  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Loads an object from a JSON resource file.
   *
   * @param <T> the type of the object to be loaded
   * @param <R> the type of the class loader
   * @param clazz the class of the object to be loaded
   * @param loader the class loader used to load the resource
   * @param name the name of the resource file
   * @return the loaded object
   */
  @SneakyThrows
  public static <T, R> T loadFromJsonResource(
      final Class<T> clazz, final Class<R> loader, final String name) {
    return mapper.readValue(loader.getResourceAsStream(name), clazz);
  }

  /**
   * Reads the content of a resource file as a String.
   *
   * @param <T> the type of the class
   * @param clazz the class used to load the resource
   * @param name the name of the resource file
   * @return the content of the resource file as a String
   */
  @SneakyThrows
  public static <T> String readResourceFile(final Class<T> clazz, final String name) {
    return new String(
        Objects.requireNonNull(clazz.getResourceAsStream(name)).readAllBytes(),
        StandardCharsets.UTF_8);
  }
}
