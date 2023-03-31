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

package de.gematik.ncpeh.api.mock.builder;

import de.gematik.ncpeh.api.common.WrappedHttpMessage;
import de.gematik.ncpeh.api.common.WrappedHttpRequest;
import de.gematik.ncpeh.api.common.WrappedHttpResponse;
import de.gematik.ncpeh.api.mock.http.PseudoHttpResponse;
import de.gematik.ncpeh.api.mock.util.ThrowingSupplier;
import de.gematik.ncpeh.api.response.SimulatorCommunicationData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.Builder;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Builder class to build objects of type {@link SimulatorCommunicationData}, which than can be used
 * as response of the API operations
 */
@Accessors(fluent = true)
@Data(staticConstructor = "newInstance")
public class SimulatorCommunicationDataBuilder implements Builder<SimulatorCommunicationData> {

  private ClientHttpRequest requestMessage;
  private PseudoHttpResponse responseMessage;

  /**
   * Take the data from the Builder, wrap the HTTP messages and build a new {@link
   * SimulatorCommunicationData} object from it. Does not throw on null fields, so it is possible to
   * build objects with missing required fields, which will produce exceptions when processed by
   * Jackson.
   *
   * @return {@link SimulatorCommunicationData}
   */
  @Override
  public SimulatorCommunicationData build() {
    return new SimulatorCommunicationData(
        Optional.ofNullable(requestMessage)
            .map(SimulatorCommunicationDataBuilder::wrapHttpRequest)
            .orElse(null),
        Optional.ofNullable(responseMessage)
            .map(SimulatorCommunicationDataBuilder::wrapHttpResponse)
            .orElse(null));
  }

  /**
   * Static method to wrap an HTTP request into an object of type {@link WrappedHttpMessage}
   *
   * @param msg {@link ClientHttpRequest} HTTP request to be wrapped up
   * @return {@link WrappedHttpMessage}
   */
  @SneakyThrows
  public static WrappedHttpMessage wrapHttpMessage(@NonNull ClientHttpRequest msg) {
    return new WrappedHttpMessage(
        msg.getHeaders().toString().getBytes(StandardCharsets.UTF_8),
        Optional.ofNullable(msg.getBody())
            .map(
                outstream ->
                    ((ThrowingSupplier<byte[], IOException>)
                            () -> ((ByteArrayOutputStream) outstream).toByteArray())
                        .toSupplier()
                        .get())
            .orElse(null));
  }

  /**
   * Static method to wrap an HTTP response into an object of type {@link WrappedHttpMessage}
   *
   * @param msg {@link ClientHttpResponse} HTTP response to be wrapped up
   * @return {@link WrappedHttpMessage}
   */
  @SneakyThrows
  public static WrappedHttpMessage wrapHttpMessage(@NonNull ClientHttpResponse msg) {
    return new WrappedHttpMessage(
        msg.getHeaders().toString().getBytes(StandardCharsets.UTF_8),
        Optional.ofNullable(msg.getBody())
            .map(
                instream ->
                    ((ThrowingSupplier<byte[], IOException>) instream::readAllBytes)
                        .toSupplier()
                        .get())
            .orElse(null));
  }

  public static WrappedHttpRequest wrapHttpRequest(@NonNull ClientHttpRequest msg) {
    return new WrappedHttpRequest(msg.getMethodValue() + " " + msg.getURI(), wrapHttpMessage(msg));
  }

  public static WrappedHttpResponse wrapHttpResponse(@NonNull PseudoHttpResponse msg) {
    return new WrappedHttpResponse(
        msg.getRawStatusCode() + " " + msg.getStatusText(), wrapHttpMessage(msg));
  }
}
