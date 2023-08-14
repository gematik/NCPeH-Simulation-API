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

package de.gematik.ncpeh.api.mock;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.event.EventType;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;
import org.slf4j.event.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

/** The main class of the Spring Boot application */
@Slf4j
@SpringBootApplication
public class NCPeHMockApplication {

  /**
   * Create the JsonProvider as Bean, which is then used to serialize and deserialize the data,
   * which are processed at the implemented API interface ({@link NCPeHMockApiImpl}).
   *
   * @return {@link JacksonJsonProvider}
   */
  @Bean
  public JacksonJsonProvider jsonProvider() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_EMPTY);
    objectMapper.setDefaultPropertyInclusion(Include.NON_DEFAULT);
    objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    objectMapper.registerModule(new JavaTimeModule());

    final JacksonJsonProvider provider = new JacksonJsonProvider();
    provider.setMapper(objectMapper);
    return provider;
  }

  @Bean
  public LoggingFeature loggingFeature() {
    final var feature = new LoggingFeature();
    final var sender =
        new Slf4jEventSender() {
          @Override
          protected String getLogMessage(LogEvent event) {
            StringBuilder buf = new StringBuilder().append("\n");
            if (List.of(EventType.REQ_IN, EventType.REQ_OUT).contains(event.getType())) {
              buf.append(event.getHttpMethod()).append(" ").append(event.getAddress()).append("\n");
            } else {
              buf.append(event.getResponseCode())
                  .append(" ")
                  .append(
                      HttpStatus.valueOf(Integer.parseInt(event.getResponseCode()))
                          .getReasonPhrase())
                  .append("\n");
            }
            event
                .getHeaders()
                .forEach((key, value) -> buf.append(key).append(": ").append(value).append("\n"));
            return buf.append("\n").append(event.getPayload()).toString();
          }
        };
    sender.setLoggingLevel(Level.DEBUG);
    feature.setSender(sender);
    feature.setPrettyLogging(true);
    feature.setLogBinary(true);
    feature.setLogMultipart(true);

    return feature;
  }

  public static void main(final String[] args) {
    SpringApplication.run(NCPeHMockApplication.class, args);
  }
}
