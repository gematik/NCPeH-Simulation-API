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

import de.gematik.ncpeh.api.response.SimulatorCommunicationData;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Optional;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

/**
 * Lightweight implementation of an HTTP request with the only purpose to create testdata for the
 * API operations in the form of content for {@link SimulatorCommunicationData#requestSend()}
 * fields.
 */
@Data
@Accessors(chain = true)
public class PseudoHttpRequest extends AbstractClientHttpRequest {

  private @NonNull HttpMethod method = HttpMethod.POST;

  private @Nullable ByteArrayOutputStream requestBody;

  private @Nullable PseudoHttpResponse response;

  private @Nullable URI uRI;

  @Override
  protected OutputStream getBodyInternal(HttpHeaders headers) {
    return requestBody;
  }

  @Override
  protected ClientHttpResponse executeInternal(HttpHeaders headers) {
    return response;
  }

  @Override
  public String getMethodValue() {
    return Optional.ofNullable(this.getMethod()).map(Enum::name).orElse("");
  }
}
