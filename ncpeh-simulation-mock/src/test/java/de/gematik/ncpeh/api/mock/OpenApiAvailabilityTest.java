/*
 * Copyright 2025 gematik GmbH
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

package de.gematik.ncpeh.api.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OpenApiAvailabilityTest {

  @Test
  void shouldReturnOpenApiJson(@Autowired final TestRestTemplate restTemplate) {
    // Arrange
    // Act
    final var response = restTemplate.getForEntity("/rest/openapi.json", String.class);
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code is not OK");
    assertEquals(
        MediaType.APPLICATION_JSON,
        response.getHeaders().getContentType(),
        "Content type is not application/json");
    assertNotNull(response.getBody(), "Response body is null");
    assertFalse(response.getBody().isEmpty(), "Response body is empty");
    assertTrue(
        Pattern.compile("\"openapi\"\\s*:\\s*\"3.\\d+.\\d+\"").matcher(response.getBody()).find(),
        "Response body does not contain OpenAPI version 3.x.x");
  }
}
