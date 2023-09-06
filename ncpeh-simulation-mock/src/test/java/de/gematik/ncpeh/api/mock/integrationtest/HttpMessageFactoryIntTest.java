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

package de.gematik.ncpeh.api.mock.integrationtest;

import static org.junit.jupiter.api.Assertions.*;

import de.gematik.ncpeh.api.mock.builder.HttpMessageFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HttpMessageFactoryIntTest {

  @Test
  void readFileContentFromPathIntTest() {
    var result =
        assertDoesNotThrow(
            () ->
                HttpMessageFactory.readUTF8FileContentFromPath(
                    HttpMessageFactory.MESSAGES_FOLDER
                        + HttpMessageFactory.PATIENT_IDENTIFICATION_RESPONSE_FILE_NAME));

    assertNotNull(result);
    assertTrue(result.contains("subjectOf1"));
  }
}
