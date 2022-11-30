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

package de.gematik.ncpeh.api.mock.http;

import java.io.InputStream;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.lang.Nullable;

/**
 * Lightweight implementation of an HTTP response with the only purpose to create testdata for the
 * API operations in the form of content for {@link
 * de.gematik.ncpeh.api.response.SimulatorCommunicationData#responseReceived} fields.
 */
@Accessors(chain = true)
@Data
public class PseudoHttpResponse extends AbstractClientHttpResponse {

  private @NonNull HttpStatus statusCode;

  private @Nullable InputStream body;

  private @Nullable HttpHeaders headers;

  @Override
  public int getRawStatusCode() {
    return statusCode.value();
  }

  @Override
  public String getStatusText() {
    return statusCode.getReasonPhrase();
  }

  @Override
  public void close() {
    // There is nothing to close.
  }
}
