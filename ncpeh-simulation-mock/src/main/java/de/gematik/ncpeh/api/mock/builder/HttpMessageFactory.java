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

import static de.gematik.ncpeh.api.mock.builder.Constants.*;

import de.gematik.ncpeh.api.mock.http.PseudoHttpRequest;
import de.gematik.ncpeh.api.mock.http.PseudoHttpResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/** Static methods used to create test data, to be returned by API operations */
@UtilityClass
public final class HttpMessageFactory {

  /**
   * Build an HTTP request to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#requestSend} element.
   *
   * @return {@link PseudoHttpRequest}
   */
  public static PseudoHttpRequest buildStandardRequest() {
    var httpRequest =
        new PseudoHttpRequest()
            .setMethod(HttpMethod.POST)
            .setURI(PSEUDO_URI)
            .setRequestBody(getStandardRequestBody())
            .setResponse(buildStandardResponse());

    var headers = httpRequest.getHeaders();

    headers.setAccept(List.of(MediaType.TEXT_XML, APPLICATION_SOAP_XML));
    headers.setContentType(APPLICATION_SOAP_XML);
    headers.setContentLength(
        Optional.ofNullable(httpRequest.getRequestBody())
            .map(ByteArrayOutputStream::size)
            .orElse(0));

    return httpRequest;
  }

  /**
   * Create an {@link java.io.OutputStream} to serve as request body of an HTTP request.
   *
   * @return {@link ByteArrayOutputStream}
   */
  public static ByteArrayOutputStream getStandardRequestBody() {
    var body = new ByteArrayOutputStream();
    body.writeBytes(PATIENT_SERVICE_RETRIEVE_MSG.getBytes(StandardCharsets.UTF_8));
    return body;
  }

  /**
   * Build an HTTP response to be used as payload in the {@link
   * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived} element.
   *
   * @return {@link PseudoHttpResponse}
   */
  public static PseudoHttpResponse buildStandardResponse() {
    var body = PATIENT_SERVICE_RETRIEVE_RESPONSE.getBytes(StandardCharsets.UTF_8);

    try (var httpResponse =
        new PseudoHttpResponse(HttpStatus.OK).setBody(new ByteArrayInputStream(body))) {

      var headers = new HttpHeaders();
      headers.setContentType(APPLICATION_SOAP_XML);
      headers.setAccept(List.of(APPLICATION_SOAP_XML, MediaType.TEXT_XML));
      headers.setContentLength(body.length);

      return httpResponse.setHeaders(headers);
    }
  }
}
