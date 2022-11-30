/*
 * Copyright (c) 2022 gematik GmbH
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

package de.gematik.ncpeh.api.mock.builder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class HttpMessageFactoryTest {

  @Test
  void buildStandardRequest() {
    var httpRequest =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardRequest,
            "Method HttpMessageFactory.buildStandardRequest threw exception");

    assertEquals(HttpMethod.POST, httpRequest.getMethod(), "Wrong method in HTTP request");
    assertEquals(Constants.PSEUDO_URI, httpRequest.getURI(), "Wrong URI in HTTP request");
    assertNotNull(httpRequest.getRequestBody(), "No body present in HTTP request");
  }

  @Test
  void getStandardRequestBody() {
    var body =
        assertDoesNotThrow(
            HttpMessageFactory::getStandardRequestBody,
            "Method HttpMessageFactory.getStandardRequestBody threw exception");

    assertNotNull(
        body, "No body was returned from function HttpMessageFactory.getStandardRequestBody");
  }

  @Test
  void buildStandardResponse() {
    var httpResponse =
        assertDoesNotThrow(
            HttpMessageFactory::buildStandardResponse,
            "Method HttpMessageFactory.buildStandardResponse threw exception");

    assertEquals(HttpStatus.OK, httpResponse.getStatusCode(), "Wrong status in HTTP response");
    assertNotNull(httpResponse.getHeaders(), "No HTTP headers present in response");
    assertFalse(httpResponse.getHeaders().isEmpty(), "No HTTP headers present in response");
  }
}
