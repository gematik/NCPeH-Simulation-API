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

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class WrappedHttpMessageTest {

  public static final byte[] BYTE_ARRAY_1 = "byte array #1".getBytes(StandardCharsets.UTF_8);

  public static final byte[] BYTE_ARRAY_2 = "byte array #2".getBytes(StandardCharsets.UTF_8);

  public static final byte[] BYTE_ARRAY_3 = "byte array #3".getBytes(StandardCharsets.UTF_8);

  public static final WrappedHttpMessage TST_OBJ_1 =
      new WrappedHttpMessage(BYTE_ARRAY_1, BYTE_ARRAY_2);

  public static final WrappedHttpMessage TST_OBJ_2 =
      new WrappedHttpMessage(BYTE_ARRAY_1, BYTE_ARRAY_2);

  public static final WrappedHttpMessage TST_OBJ_3 =
      new WrappedHttpMessage(BYTE_ARRAY_1, BYTE_ARRAY_3);

  @Test
  @SuppressWarnings({"all"})
  void testEquals() {
    var result = assertDoesNotThrow(() -> TST_OBJ_1.equals(null));

    assertFalse(result, "Object equals null -> this is wrong!");

    result = assertDoesNotThrow(() -> TST_OBJ_1.equals(TST_OBJ_1));

    assertTrue(result, "Object does not equal itself -> this is wrong!");

    result = assertDoesNotThrow(() -> TST_OBJ_1.equals(TST_OBJ_2));

    assertTrue(result, "Object does not equal object of same class and content -> this is wrong!");

    result = assertDoesNotThrow(() -> TST_OBJ_1.equals(TST_OBJ_3));

    assertFalse(result, "Object equals Object with different content -> this is wrong!");
  }

  @Test
  void testHashCode() {
    var result = assertDoesNotThrow(TST_OBJ_1::hashCode);

    assertEquals(result, TST_OBJ_2.hashCode(), "Hash codes were different");
  }

  @Test
  void testToString() {
    var result = assertDoesNotThrow(TST_OBJ_1::toString);

    assertNotNull(result, "toString method returned null");
    assertTrue(
        result.contains(Arrays.toString(TST_OBJ_1.httpHeader())),
        "toString value of the httpHeader field missing");
    assertTrue(
        result.contains(Arrays.toString(TST_OBJ_1.httpBody())),
        "toString value of the httpBody field missing");
  }
}
