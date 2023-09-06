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

package de.gematik.ncpeh.api.mock.util;

import java.util.function.Supplier;
import lombok.SneakyThrows;

/**
 * Simple functional supplier interface to be able to include code in the supplier, which throws
 * predefined exceptions
 *
 * @param <T> Type which will be returned by the get function of the supplier
 * @param <E> {@link Throwable} type, which might be thrown by the provided code
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

  T get() throws E;

  /**
   * Transforms this Supplier into a regular Java lang {@link Supplier}, without having to worry
   * about the exceptions, that might be thrown.
   *
   * @return {@link Supplier}
   */
  default Supplier<T> toSupplier() {
    var throwingSupplier = this;
    return new Supplier<T>() {
      @SneakyThrows
      @Override
      public T get() {
        return throwingSupplier.get();
      }
    };
  }
}
